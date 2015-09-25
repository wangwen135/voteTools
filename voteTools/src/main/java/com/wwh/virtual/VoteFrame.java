package com.wwh.virtual;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 这个网站 投假票的 http://zt.xtol.cn/zmcjr/
 */
public class VoteFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(VoteFrame.class);

    private boolean enableProxy = false;

    private JPanel contentPane;
    private JTextField txt_xff;
    private JLabel label_img;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 780, 499);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton btnsessionid = new JButton("获取验证码和SessionID");
        btnsessionid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getImageAndSessionId();
                } catch (Exception e1) {
                    log.error("获取验证码和session ID 异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "获取验证码和Session ID 异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        btnsessionid.setBounds(27, 29, 254, 23);
        contentPane.add(btnsessionid);

        txt_xff = new JTextField();
        txt_xff.setBounds(27, 234, 163, 21);
        contentPane.add(txt_xff);
        txt_xff.setColumns(20);

        JLabel lblXff = new JLabel("X-FORWARDED-FOR");
        lblXff.setBounds(28, 209, 162, 15);
        contentPane.add(lblXff);

        label_img = new JLabel("");
        label_img.setBorder(new LineBorder(new Color(0, 0, 0)));
        label_img.setHorizontalAlignment(SwingConstants.CENTER);
        label_img.setIcon(new ImageIcon(VoteFrame.class.getResource("/sun/print/resources/duplex.png")));
        label_img.setBounds(120, 92, 150, 57);
        contentPane.add(label_img);

        JLabel label = new JLabel("输入验证码：");
        label.setBounds(27, 162, 85, 15);
        contentPane.add(label);

        txt_secode = new JTextField();
        txt_secode.setBounds(120, 159, 150, 21);
        contentPane.add(txt_secode);
        txt_secode.setColumns(10);

        JLabel lblSessionId = new JLabel("Session ID 是：");
        lblSessionId.setBounds(27, 64, 95, 15);
        contentPane.add(lblSessionId);

        txt_sessionID = new JTextField();
        txt_sessionID.setBounds(120, 61, 182, 21);
        contentPane.add(txt_sessionID);
        txt_sessionID.setColumns(10);

        JLabel label_1 = new JLabel("验证码是：");
        label_1.setBounds(27, 92, 95, 15);
        contentPane.add(label_1);

        JButton button_1 = new JButton("投票");
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    vote();
                } catch (Exception e1) {
                    log.error("投票异常", e1);
                    JOptionPane.showMessageDialog(VoteFrame.this, "投票异常\n" + e1.getMessage(), "错误！", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        button_1.setBounds(97, 319, 93, 23);
        contentPane.add(button_1);

        JButton btnip = new JButton("随机IP");
        btnip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });
        btnip.setBounds(226, 233, 93, 23);
        contentPane.add(btnip);

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
        button.setBounds(382, 29, 163, 23);
        contentPane.add(button);
    }

    private static final String HOST = "http://app.xtol.cn";
    private static final String IMAGE_URL = HOST + "/?app=system&controller=seccode&action=image&no_border=1&length=11&_=";

    // &optionid[]=742
    private static final String VOTE_URL = HOST
            + "/?app=vote&controller=vote&action=ajaxvote&jsoncallback=jsonp%d&_=%d&contentid=4843780%s&seccode=%s&seccode_type=pro";

    private static final String VOTES_INFO_URL = HOST + "/?jsoncallback=jsonp%d&_=%d&app=vote&controller=vote&action=ajaxresult&contentid=4843780";

    private JTextField txt_secode;
    private JTextField txt_sessionID;

    CloseableHttpClient httpclient;

    public void loadInfo() throws Exception, IOException {

        Date d = new Date();
        Long time = d.getTime();
        String turl = String.format(VOTES_INFO_URL, time - 331453, time);

        System.out.println("请求路径是：" + turl);

        HttpGet httpGet = new HttpGet(turl);

        // 设置假IP
        httpGet.addHeader("X-FORWARDED-FOR", txt_xff.getText());

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            String rpString = EntityUtils.toString(entity);

            System.out.println(rpString);

            rpString = rpString.substring(rpString.indexOf("(") + 1, rpString.lastIndexOf(")"));

            System.out.println(rpString);

            ResultJSON rjson = JSON.parseObject(rpString, ResultJSON.class);

            System.out.println(rjson);

            JSONObject parseObject = JSON.parseObject(rpString);
            Boolean b = parseObject.getBoolean("state");

            if (b) {

            } else {
                JOptionPane.showMessageDialog(VoteFrame.this, parseObject.getString("error"), "获取票数信息异常", JOptionPane.ERROR_MESSAGE);
            }

            System.out.println(parseObject);

            EntityUtils.consume(entity);
        } finally {
            response.close();

        }

    }

    public void vote() throws Exception, IOException {
        Date d = new Date();
        Long time = d.getTime();
        String turl = String.format(VOTE_URL, time - 331813, time, "&optionid[]=742", txt_secode.getText());
        System.out.println("请求路径是：" + turl);

        HttpGet httpGet = new HttpGet(turl);

        // 设置假IP
        httpGet.addHeader("X-FORWARDED-FOR", txt_xff.getText());

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            String rpString = EntityUtils.toString(entity);

            System.out.println(rpString);

            rpString = rpString.substring(rpString.indexOf("(") + 1, rpString.lastIndexOf(")"));

            System.out.println(rpString);

            JSONObject parseObject = JSON.parseObject(rpString);
            Boolean b = parseObject.getBoolean("state");

            if (b) {

            } else {
                JOptionPane.showMessageDialog(VoteFrame.this, parseObject.getString("error"), "投票失败", JOptionPane.ERROR_MESSAGE);
            }

            System.out.println(parseObject);

            EntityUtils.consume(entity);
        } finally {
            response.close();

        }
    }

    public void getImageAndSessionId() throws Exception, IOException {
        if (httpclient != null) {
            httpclient.close();
        }

        if (enableProxy) {
            HttpHost proxy = new HttpHost("127.0.0.1", 9999, "http");
            httpclient = HttpClientBuilder.create().setProxy(proxy).build();
        } else {
            httpclient = HttpClients.createDefault();
        }

        Date d = new Date();
        HttpGet httpGet = new HttpGet(IMAGE_URL + d.getTime());

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());

            // 获取session
            HeaderElement[] headere = response.getFirstHeader("Set-Cookie").getElements();
            for (HeaderElement he : headere) {
                if ("PHPSESSID".equals(he.getName())) {
                    txt_sessionID.setText(he.getValue());
                }
            }

            HttpEntity entity = response.getEntity();

            BufferedImage image = ImageIO.read(entity.getContent());

            label_img.setIcon(new ImageIcon(image));

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }
}
