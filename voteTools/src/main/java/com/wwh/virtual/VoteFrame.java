package com.wwh.virtual;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.wwh.ipconfig.IpConfig;
import com.wwh.ipconfig.ReadIpConfig;
import com.wwh.virtual.tablemodel.CandidateModel;

/**
 * 这个网站 投假票的 http://zt.xtol.cn/zmcjr/
 */
public class VoteFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(VoteFrame.class);

    private static final String HOST = "http://app.xtol.cn";
    // 验证码地址
    private static final String IMAGE_URL = HOST + "/?app=system&controller=seccode&action=image&no_border=1&length=11&_=";
    // 发杂验证码地址
    private static final String COMPLEX_IMAGE_URL = HOST + "/?app=system&controller=seccode&action=image_pro&no_border=1&length=11&_=";

    // 投票地址
    private static final String VOTE_URL = HOST
            + "/?app=vote&controller=vote&action=ajaxvote&jsoncallback=jsonp%d&_=%d&contentid=4843780%s&seccode=%s&seccode_type=pro";

    // 装载列表地址
    private static final String VOTES_INFO_URL = HOST + "/?jsoncallback=jsonp%d&_=%d&app=vote&controller=vote&action=ajaxresult&contentid=4843780";

    // 设置超时时间
    private int socketTimeout = 5000;
    private int connectTimeout = 5000;

    // 避免从http请求头中识别
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36";
    private static final String REFERER = "http://zt.xtol.cn/zmcjr/";
    private static final String ACCEPT_LANGUAGE = "zh-CN,zh;q=0.8,en;q=0.6";

    // Ip地址段配置对象
    private IpConfig ipcnf;

    private JPanel contentPane;
    private JTextField txt_xff;
    private JLabel label_img;
    private JTextField txt_secode;

    private CloseableHttpClient httpclient;
    private JTextField txt_proxyIp;
    private JTextField txt_proxyPort;
    private JTable table;
    private CandidateModel tableModel;
    private JCheckBox ckBox_enableProxy;
    private JCheckBox ckbox_simpleSecode;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(windows);
        } catch (Exception e1) {
            //
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VoteFrame frame = new VoteFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public VoteFrame() {
        setTitle("投票工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 997, 512);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton button = new JButton("获取投票人列表信息：");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    loadInfo();
                } catch (Exception e1) {
                    log.error("获取投票人列表异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "获取投票人列表异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        button.setBounds(429, 10, 208, 23);
        contentPane.add(button);

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        panel.setBounds(10, 10, 360, 110);
        contentPane.add(panel);
        panel.setLayout(null);

        ckBox_enableProxy = new JCheckBox("启用代理");
        ckBox_enableProxy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableProxy();
            }
        });
        ckBox_enableProxy.setBounds(10, 6, 73, 23);
        panel.add(ckBox_enableProxy);

        JLabel lblip = new JLabel("代理IP：");
        lblip.setBounds(10, 41, 54, 15);
        panel.add(lblip);

        txt_proxyIp = new JTextField();
        txt_proxyIp.setEnabled(false);
        txt_proxyIp.setText("127.0.0.1");
        txt_proxyIp.setBounds(65, 38, 148, 21);
        panel.add(txt_proxyIp);
        txt_proxyIp.setColumns(10);

        JLabel label_2 = new JLabel("端口：");
        label_2.setBounds(234, 41, 43, 15);
        panel.add(label_2);

        txt_proxyPort = new JTextField();
        txt_proxyPort.setEnabled(false);
        txt_proxyPort.setText("9999");
        txt_proxyPort.setBounds(276, 38, 66, 21);
        panel.add(txt_proxyPort);
        txt_proxyPort.setColumns(10);

        JButton btn_openPrxy = new JButton("设置网络开放代理");
        btn_openPrxy.setEnabled(false);
        btn_openPrxy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VoteFrame.this, "没做");
            }
        });
        btn_openPrxy.setBounds(10, 73, 203, 23);
        panel.add(btn_openPrxy);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(429, 45, 542, 419);
        contentPane.add(scrollPane);

        table = new JTable();
        table.setAutoCreateRowSorter(true);
        tableModel = new CandidateModel();
        table.setModel(tableModel);
        scrollPane.setViewportView(table);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new CompoundBorder(new LineBorder(new Color(255, 0, 0), 1, true), new EmptyBorder(5, 5, 5, 5)));
        panel_1.setBounds(10, 160, 360, 180);
        contentPane.add(panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 80, 0, 0, 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0, 65, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        JLabel lblXff = new JLabel("IP地址：");
        GridBagConstraints gbc_lblXff = new GridBagConstraints();
        gbc_lblXff.anchor = GridBagConstraints.WEST;
        gbc_lblXff.insets = new Insets(0, 0, 5, 5);
        gbc_lblXff.gridx = 0;
        gbc_lblXff.gridy = 0;
        panel_1.add(lblXff, gbc_lblXff);

        txt_xff = new JTextField();
        GridBagConstraints gbc_txt_xff = new GridBagConstraints();
        gbc_txt_xff.fill = GridBagConstraints.HORIZONTAL;
        gbc_txt_xff.insets = new Insets(0, 0, 5, 5);
        gbc_txt_xff.gridx = 1;
        gbc_txt_xff.gridy = 0;
        panel_1.add(txt_xff, gbc_txt_xff);
        txt_xff.setColumns(20);

        JButton btnip = new JButton("随机IP");
        GridBagConstraints gbc_btnip = new GridBagConstraints();
        gbc_btnip.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnip.insets = new Insets(0, 0, 5, 0);
        gbc_btnip.gridx = 3;
        gbc_btnip.gridy = 0;
        panel_1.add(btnip, gbc_btnip);

        ckbox_simpleSecode = new JCheckBox("使用简单验证码");
        ckbox_simpleSecode.setSelected(true);
        GridBagConstraints gbc_ckbox_simpleSecode = new GridBagConstraints();
        gbc_ckbox_simpleSecode.anchor = GridBagConstraints.SOUTH;
        gbc_ckbox_simpleSecode.insets = new Insets(0, 0, 5, 5);
        gbc_ckbox_simpleSecode.gridx = 1;
        gbc_ckbox_simpleSecode.gridy = 1;
        panel_1.add(ckbox_simpleSecode, gbc_ckbox_simpleSecode);

        JLabel label_1 = new JLabel("验证码图片：");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.WEST;
        gbc_label_1.insets = new Insets(0, 0, 5, 5);
        gbc_label_1.gridx = 0;
        gbc_label_1.gridy = 2;
        panel_1.add(label_1, gbc_label_1);

        label_img = new JLabel("");
        GridBagConstraints gbc_label_img = new GridBagConstraints();
        gbc_label_img.fill = GridBagConstraints.BOTH;
        gbc_label_img.insets = new Insets(0, 0, 5, 5);
        gbc_label_img.gridx = 1;
        gbc_label_img.gridy = 2;
        panel_1.add(label_img, gbc_label_img);
        label_img.setBorder(new LineBorder(new Color(0, 0, 0)));
        label_img.setHorizontalAlignment(SwingConstants.CENTER);
        label_img.setIcon(new ImageIcon(VoteFrame.class.getResource("/sun/print/resources/duplex.png")));

        JButton btnSecode = new JButton("刷新验证码");
        GridBagConstraints gbc_btnSecode = new GridBagConstraints();
        gbc_btnSecode.insets = new Insets(0, 0, 5, 0);
        gbc_btnSecode.gridx = 3;
        gbc_btnSecode.gridy = 2;
        panel_1.add(btnSecode, gbc_btnSecode);

        JLabel label = new JLabel("输入验证码：");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 3;
        panel_1.add(label, gbc_label);

        txt_secode = new JTextField();
        txt_secode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    vote();
                } catch (Exception e1) {
                    log.error("投票异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "投票异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        GridBagConstraints gbc_txt_secode = new GridBagConstraints();
        gbc_txt_secode.fill = GridBagConstraints.HORIZONTAL;
        gbc_txt_secode.insets = new Insets(0, 0, 0, 5);
        gbc_txt_secode.gridx = 1;
        gbc_txt_secode.gridy = 3;
        panel_1.add(txt_secode, gbc_txt_secode);
        txt_secode.setColumns(10);

        JButton btn_vote = new JButton("投票");
        GridBagConstraints gbc_btn_vote = new GridBagConstraints();
        gbc_btn_vote.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn_vote.gridx = 3;
        gbc_btn_vote.gridy = 3;
        panel_1.add(btn_vote, gbc_btn_vote);

        JLabel lblNewLabel = new JLabel("<html>\r\n<b>说明：</b><br>\r\n在右侧勾选要投票的人 （点击标题可以排序）<br>\r\n在验证码输入框中输入验证码按回车即可<br>\r\n验证码错误或IP重复，接着按回车就会自动刷新\r\n<html>");
        lblNewLabel.setBounds(10, 354, 360, 110);
        contentPane.add(lblNewLabel);
        btn_vote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    vote();
                } catch (Exception e1) {
                    log.error("投票异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "投票异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        btnSecode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getImageAndSessionId();
                } catch (Exception e1) {
                    log.error("获取验证码和session ID 异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "获取验证码异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        btnip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setXXFIP();
            }
        });

        // 初始化系统
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

    }

    private void init() {
        try {
            ipcnf = ReadIpConfig.readIpConfig();
        } catch (Exception e) {
            log.error("获取IP配置异常", e);
            JOptionPane.showMessageDialog(VoteFrame.this, "获取IP配置异常\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }

        // 设置随机ip
        setXXFIP();

        // 初始化连接
        initHttpClient();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 加载信息列表
                try {
                    loadInfo();
                } catch (Exception e1) {
                    log.error("获取投票人列表异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "获取投票人列表异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 获取验证码
                try {
                    getImageAndSessionId();
                } catch (Exception e1) {
                    log.error("获取验证码和session ID 异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "获取验证码异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 定位焦点
        txt_secode.requestFocus();
    }

    /**
     * 加载信息
     * 
     * @throws Exception
     */
    public void loadInfo() throws Exception {

        if (httpclient == null)
            initHttpClient();

        Date d = new Date();
        Long time = d.getTime();
        String turl = String.format(VOTES_INFO_URL, time - 331453, time);

        log.debug("加载信息的请求路径是：{}", turl);

        HttpGet httpGet = new HttpGet(turl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();// 设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        // 设置http头
        setHttpHeader(httpGet);

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                JOptionPane.showMessageDialog(VoteFrame.this, "加载失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            HttpEntity entity = response.getEntity();

            String rpString = EntityUtils.toString(entity);

            rpString = rpString.substring(rpString.indexOf("(") + 1, rpString.lastIndexOf(")"));

            LoadInfoResult loadinfo = JSON.parseObject(rpString, LoadInfoResult.class);

            EntityUtils.consume(entity);

            tableModel.convertHashMap(loadinfo.getOption());
        } finally {
            response.close();

        }

    }

    /**
     * @param httpGet
     */
    public void setHttpHeader(HttpGet httpGet) {
        String v_ip = txt_xff.getText();
        if (v_ip.length() > 7)
            httpGet.addHeader("X-FORWARDED-FOR", v_ip);

        httpGet.addHeader("User-Agent", USER_AGENT);

        httpGet.addHeader("Referer", REFERER);

        httpGet.addHeader("Accept-Language", ACCEPT_LANGUAGE);
    }

    private String formateOptionid(List<String> options) {
        StringBuffer sbuf = new StringBuffer();
        for (String s : options) {
            sbuf.append("&optionid[]=");
            sbuf.append(s);
        }
        return sbuf.toString();

    }

    public void vote() throws Exception, IOException {
        // 先要输入验证码
        String secode = txt_secode.getText();
        if (secode.length() != 4) {
            JOptionPane.showMessageDialog(VoteFrame.this, "请输入验证码", "错误", JOptionPane.WARNING_MESSAGE);
            txt_secode.requestFocus();
            return;
        }

        // 获取选中列表
        List<String> options = tableModel.getAllSelectedOptionid();
        // 判断
        if (options == null || options.isEmpty()) {
            JOptionPane.showMessageDialog(VoteFrame.this, "请在右侧勾选需要投票的人", "错误", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (options.size() > 10) {
            JOptionPane.showMessageDialog(VoteFrame.this, "一次投票的人数不能超过10个", "错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date d = new Date();
        Long time = d.getTime();

        String turl = String.format(VOTE_URL, time - 331813, time, formateOptionid(options), secode);

        log.debug("请求的路径是：{}", turl);

        HttpGet httpGet = new HttpGet(turl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();// 设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        // 设置请求头
        setHttpHeader(httpGet);

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                JOptionPane.showMessageDialog(VoteFrame.this, "投票失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            HttpEntity entity = response.getEntity();

            String rpString = EntityUtils.toString(entity, "UTF-8");

            rpString = rpString.substring(rpString.indexOf("(") + 1, rpString.lastIndexOf(")"));

            log.debug(rpString);

            VoteResult vRet = JSON.parseObject(rpString, VoteResult.class);

            if (vRet.isState()) {
                JOptionPane.showMessageDialog(VoteFrame.this, "投票成功");
                tableModel.convertHashMap(vRet.getData());
                // 刷新
                refresh();
            } else {
                JOptionPane.showMessageDialog(VoteFrame.this, vRet.getError(), "投票失败", JOptionPane.ERROR_MESSAGE);
                // 刷新
                refresh();
            }

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    public void refresh() {
        // 初始化连接
        // 断开重连
        initHttpClient();

        // 设置随机ip
        setXXFIP();

        // 情况验证码
        txt_secode.setText("");

        // 获取验证码
        try {
            getImageAndSessionId();
        } catch (Exception e1) {
            log.error("获取验证码和session ID 异常", e1);
            JOptionPane.showMessageDialog(VoteFrame.this, "获取验证码异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
        }

        // 定位焦点
        txt_secode.requestFocus();
    }

    public void getImageAndSessionId() throws Exception, IOException {
        if (httpclient == null)
            initHttpClient();

        Date d = new Date();

        String url;
        if (ckbox_simpleSecode.isSelected()) {
            url = IMAGE_URL + d.getTime();
        } else {
            url = COMPLEX_IMAGE_URL + d.getTime();
        }

        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();// 设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        // 设置请求头
        setHttpHeader(httpGet);

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                JOptionPane.showMessageDialog(VoteFrame.this, "加载失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 获取session
            // HeaderElement[] headere =
            // response.getFirstHeader("Set-Cookie").getElements();
            // for (HeaderElement he : headere) {
            // if ("PHPSESSID".equals(he.getName())) {
            // txt_sessionID.setText(he.getValue());
            // }
            // }

            HttpEntity entity = response.getEntity();

            BufferedImage image = ImageIO.read(entity.getContent());

            label_img.setIcon(new ImageIcon(image));

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    /**
     * 
     */
    public void initHttpClient() {
        if (httpclient != null)
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("关闭httpClient 异常", e);
            }

        if (ckBox_enableProxy.isSelected()) {

            // 判断IP
            String ip = txt_proxyIp.getText();
            // 判断端口
            int port = Integer.parseInt(txt_proxyPort.getText());

            HttpHost proxy = new HttpHost(ip, port, "http");
            httpclient = HttpClientBuilder.create().setProxy(proxy).build();
        } else {
            httpclient = HttpClients.createDefault();
        }

    }

    /**
     * 
     */
    public void enableProxy() {
        boolean enable = ckBox_enableProxy.isSelected();

        txt_proxyIp.setEnabled(enable);
        txt_proxyPort.setEnabled(enable);

        // 初始化连接
        initHttpClient();
    }

    public void setXXFIP() {
        if (ipcnf == null) {
            randomIPAddress();
        } else {
            txt_xff.setText(ipcnf.randomIP());
        }
    }

    /**
     * 
     */
    public void randomIPAddress() {
        Random r = new Random();
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(r.nextInt(255));
        sbuf.append(".");
        sbuf.append(r.nextInt(255));
        sbuf.append(".");
        sbuf.append(r.nextInt(255));
        sbuf.append(".");
        sbuf.append(r.nextInt(255));

        txt_xff.setText(sbuf.toString());
    }
}
