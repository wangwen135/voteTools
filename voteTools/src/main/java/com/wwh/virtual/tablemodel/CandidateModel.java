package com.wwh.virtual.tablemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.wwh.virtual.CandidateEntity;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author wwh
 * @date 2015年9月28日 上午11:56:24
 *
 */
public class CandidateModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private List<CandidateEntity> list;

    public CandidateModel() {
    }

    public CandidateModel(List<CandidateEntity> list) {
        this.list = list;
    }

    /**
     * 获取 list
     *
     * @return the list
     */
    public List<CandidateEntity> getList() {
        return list;
    }

    /**
     * 设置 list
     *
     * @param list
     *            the list to set
     */
    public void setList(List<CandidateEntity> list) {
        this.list = list;
        fireTableDataChanged();
    }

    /**
     * 获取所有选择的
     * 
     * @return
     */
    public List<CandidateEntity> getAllSelected() {
        List<CandidateEntity> lt = new ArrayList<CandidateEntity>();
        if (list != null)
            for (CandidateEntity te : list) {
                if (te.isSelected()) {
                    lt.add(te);
                }
            }
        return lt;
    }

    public List<String> getAllSelectedOptionid() {
        List<String> li = new ArrayList<String>();
        if (list != null)
            for (CandidateEntity te : list) {
                if (te.isSelected()) {
                    li.add(te.getOptionid());
                }
            }
        return li;
    }

    @Override
    public int getRowCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "序号";
        case 1:
            return "勾选";
        case 2:
            return "编号";
        case 3:
            return "姓名";
        case 4:
            return "票数";

        default:
            return "";
        }
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 4) {
            return Integer.class;
        } else if (columnIndex == 1) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return true;
        } else
            return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (list == null || list.size() < rowIndex) {
            return null;
        }
        CandidateEntity entity = list.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return rowIndex + 1;
        case 1:
            return entity.isSelected();
        case 2:
            return entity.getOptionid();
        case 3:
            return entity.getName();
        case 4:
            return entity.getVotes();
        default:
            return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (list == null || list.size() < rowIndex) {
            return;
        }
        if (columnIndex != 1) {
            return;
        }

        CandidateEntity entity = list.get(rowIndex);

        entity.setSelected((Boolean) aValue);

        fireTableDataChanged();
    }

    /**
     * 转换hashMap 保留选中
     * 
     * @param map
     */
    public void convertHashMap(Map<String, CandidateEntity> map) {

        List<CandidateEntity> selectedList = getAllSelected();
        for (CandidateEntity ce : selectedList) {
            map.get(ce.getOptionid()).setSelected(true);
        }

        List<CandidateEntity> list = new ArrayList<CandidateEntity>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }

        setList(list);
    }

}
