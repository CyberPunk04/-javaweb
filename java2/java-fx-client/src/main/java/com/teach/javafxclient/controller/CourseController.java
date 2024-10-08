package com.teach.javafxclient.controller;

import com.teach.javafxclient.controller.base.MessageDialog;
import com.teach.javafxclient.controller.base.ToolController;
import com.teach.javafxclient.model.Teacher;
import com.teach.javafxclient.request.DataRequest;
import com.teach.javafxclient.request.DataResponse;
import com.teach.javafxclient.request.HttpRequestUtil;
import com.teach.javafxclient.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CourseController 登录交互控制类 对应 course-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //课程信息表
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;  //学分
    @FXML
    private TableColumn<Map,String> hourColumn; //学时
    @FXML
    private TableColumn<Map,String> typeColumn; //课程类型
   /* @FXML
    private TableColumn<Map,String> teacherNumColumn; //教师ID*/
    @FXML
    private TableColumn<Map,String> teacherNameColumn; //任课教师
    @FXML
    private TableColumn<Map,String> statusColumn; //课程状态
    @FXML
    private TableColumn<Map,String> descriptionColumn; //课程描述
    @FXML
    private TableColumn<Map,String> remarkColumn; //备注


    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField creditField;
    @FXML
    private TextField hourField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField statusField;
    @FXML
    private TextField teacherNumField;
    @FXML
    private TextField teacherNameField;
    @FXML
    private TextField descField;
    @FXML
    private TextField remarkField;
    @FXML
    private TextField numNameTextField;

    private Integer courseId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> courseList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    /**
     * 将课程数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < courseList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(courseList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req =new DataRequest();
        req.put("numName","");
        res = HttpRequestUtil.request("/api/course/getCourseList",req);
        if(res != null && res.getCode()== 0) {
            courseList = (ArrayList<Map>)res.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("courseNum"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        hourColumn.setCellValueFactory(new MapValueFactory<>("courseHour"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("courseType"));
        //teacherNumColumn.setCellValueFactory(new MapValueFactory<>("teacherNum"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("courseStatus"));
        descriptionColumn.setCellValueFactory(new MapValueFactory<>("courseDesc"));
        remarkColumn.setCellValueFactory(new MapValueFactory<>("courseRemark"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();

    }

    /**
     * 清除输入信息
     */
    public void clearPanel(){
        courseId = null;
        numField.setText("");
        nameField.setText("");
        creditField.setText("");
        hourField.setText("");
        typeField.setText("");
        teacherNumField.setText("");
        teacherNameField.setText("");
        statusField.setText("");
        descField.setText("");
        remarkField.setText("");
    }

    protected void changeCourseInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        courseId = CommonMethod.getInteger(form,"courseId");
        DataRequest req = new DataRequest();
        req.put("courseId",courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseInfo",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map)res.getData();
        numField.setText(CommonMethod.getString(form, "courseNum"));
        nameField.setText(CommonMethod.getString(form, "courseName"));
        creditField.setText(CommonMethod.getString(form, "credit"));
        hourField.setText(CommonMethod.getString(form, "courseHour"));
        typeField.setText(CommonMethod.getString(form, "courseType"));
        teacherNumField.setText(CommonMethod.getString(form, "teacherNum"));
        teacherNameField.setText(CommonMethod.getString(form, "teacherName"));
        statusField.setText(CommonMethod.getString(form, "courseStatus"));
        descField.setText(CommonMethod.getString(form, "courseDesc"));
        remarkField.setText(CommonMethod.getString(form, "courseRemark"));

    }
    /**
     * 点击课程列表的某一行，根据courseId ,从后台查询课程的基本信息，切换课程的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeCourseInfo();
    }

    /**
     * 点击查询按钮，
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.put("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList",req);
        if(res != null && res.getCode()== 0) {
            courseList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
    /**
     *  添加
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除课程
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        courseId = CommonMethod.getInteger(form,"courseId");
        DataRequest req = new DataRequest();
        req.put("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/courseDelete",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("该课程信息删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * 点击保存按钮，保存课程信息
     */
    @FXML
    protected void onSaveButtonClick() {
        if( numField.getText().equals("")) {
            MessageDialog.showDialog("课程号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("courseNum",numField.getText());
        form.put("courseName",nameField.getText());
        form.put("credit",creditField.getText());
        form.put("courseHour",hourField.getText());
        form.put("courseType",typeField.getText());
        form.put("courseStatus",statusField.getText());
        form.put("courseDesc",descField.getText());
        form.put("courseRemark",remarkField.getText());
        form.put("teacherNum",teacherNumField.getText());
        form.put("teacherName",teacherNameField.getText());
        DataRequest req = new DataRequest();
        req.put("courseId", courseId);
       // req.put("TeacherId", teacherId);
        req.put("form", form);
        DataResponse res = HttpRequestUtil.request("/api/course/courseEditSave",req);
        if(res.getCode() == 0) {
            courseId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法
     */
    public void doNew(){
        clearPanel();
    }
    public void doSave(){
        onSaveButtonClick();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }

    //导出excel
    // 导入excel

}
