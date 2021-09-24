package com.github.duoluo9.BaseLibraryPlugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BaseMvpActivityAction extends AnAction {


    private Project project;
    private Module module;
    //包名
    private String packageName = "";
    private String pageName;
    private boolean isContract;


    private enum CodeType {
        Activity, View, IPresenter, Presenter, IModel, Model, Activity_Layout, Contract
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        module = (Module) e.getDataContext().getData("module");
        packageName = getPackageName();
        init();
        refreshProject(e);
    }

    /**
     * 刷新项目
     *
     * @param e
     */
    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    /**
     * 初始化Dialog
     */
    private void init() {
        BaseMvpActivityDialog myDialog = new BaseMvpActivityDialog((pageName, isContract) -> {
            BaseMvpActivityAction.this.pageName = pageName;
            BaseMvpActivityAction.this.isContract = isContract;
            createClassFiles();
        });
        myDialog.setVisible(true);

    }

    /**
     * 生成类文件
     */
    private void createClassFiles() {
        createClassFile(CodeType.Activity);
        createClassFile(CodeType.Activity_Layout);
        if (isContract) {
            createClassFile(CodeType.Contract);
        } else {
            createClassFile(CodeType.View);
            createClassFile(CodeType.IPresenter);
            createClassFile(CodeType.IModel);
        }
        createClassFile(CodeType.Presenter);
        createClassFile(CodeType.Model);
    }

    /**
     * 生成mvp框架代码
     *
     * @param codeType
     */
    private void createClassFile(CodeType codeType) {
        String fileName = "";
        String content = "";
        String appPath = getAppPath();
        switch (codeType) {
            case Activity:
                fileName = "BaseMvpActivity.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "activity", pageName + "Activity.kt");
                editManifest(pageName + "Activity");
                break;
            case Contract:
                fileName = "MainContract.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "mvp/contract", "I" + pageName + "Contract.kt");
                break;
            case View:
                fileName = "IMainView.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "mvp/view", "I" + pageName + "View.kt");
                break;
            case IPresenter:
                fileName = "IMainPresenter.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "mvp/presenter/ipresenter", "I" + pageName + "Presenter.kt");
                break;
            case Presenter:
                fileName = "MainPresenter.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "mvp/presenter", pageName + "Presenter.kt");
                break;
            case IModel:
                fileName = "IMainModel.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "mvp/model/imodel", "I" + pageName + "Model.kt");
                break;
            case Model:
                fileName = "MainModel.kt.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                writeToFile(content, appPath + "mvp/model", pageName + "Model.kt");
                break;
            case Activity_Layout:
                fileName = "simple.xml.ftl";
                content = ReadTemplateFile(fileName);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("activity");
                ArrayList<String> activityChildNames = splitByUpperCase(pageName);
                for (String activityChildName : activityChildNames) {
                    stringBuilder.append("_").append(activityChildName.toLowerCase());
                }
                stringBuilder.append(".xml");

                writeToFile(content, getAppResPath(), stringBuilder.toString());
                break;
        }
    }

    /**
     * 获取包名文件路径
     *
     * @return
     */
    private String getAppPath() {
        String packagePath = packageName.replace(".", "/");
        String appPath = project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/java/" + packagePath + "/";
        return appPath;
    }

    /**
     * 获取包名文件路径
     *
     * @return
     */
    private String getAppResPath() {
        String appPath = project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/res/layout/";
        return appPath;
    }

    /**
     * 替换模板中字符
     *
     * @param content
     * @return
     */
    private String dealTemplateContent(String content) {
        content = content.replace("${pageName}", pageName);
        if (content.contains("${packageName}")) {
            content = content.replace("${packageName}", packageName);
        }
        if (content.contains("${activityLayoutName}")) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("activity");
            ArrayList<String> activityChildNames = splitByUpperCase(pageName);
            for (String activityChildName : activityChildNames) {
                stringBuilder.append("_").append(activityChildName.toLowerCase());
            }

            content = content.replace("${activityLayoutName}", stringBuilder.toString());
        }

        if (content.contains("${modelPath}")) {
            content = content.replace("${modelPath}", isContract ? "contract." + pageName + "Contract" : "model.imodel");
        }
        if (content.contains("${presenterPath}")) {
            content = content.replace("${presenterPath}", isContract ? "contract." + pageName + "Contract" : "presenter.ipresenter");
        }
        if (content.contains("${viewPath}")) {
            content = content.replace("${viewPath}", isContract ? "contract." + pageName + "Contract" : "view");
        }
        return content;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    /**
     * 读取模板文件中的字符内容
     *
     * @param fileName 模板文件名
     * @return
     */
    private String ReadTemplateFile(String fileName) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("/com/github/duoluo9/BaseLibraryPlugin/Template/" + fileName);
        String content = "";
        try {
            if (in != null)
                content = new String(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }


    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return outputStream.toByteArray();
    }


    /**
     * 生成
     *
     * @param content   类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private void writeToFile(String content, String classPath, String className) {
        try {
            File floder = new File(classPath);
            if (!floder.exists()) {
                floder.mkdirs();
            }

            File file = new File(classPath + "/" + className);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从AndroidManifest.xml文件中获取当前app的包名
     *
     * @return
     */
    private String getPackageName() {
        String package_name = "";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                package_name = element.getAttribute("package");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return package_name;
    }

    private void editManifest(String name) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/AndroidManifest.xml");
            NodeList nodeList1 = doc.getElementsByTagName("application");

            for (int j = 0; j < nodeList1.getLength(); ++j) {
                Node node = nodeList1.item(j);
                Element application = (Element) node;
                Element a = doc.createElement("activity");
                a.setAttribute("android:name", this.packageName + ".activity." + name);
                application.appendChild(a);
            }

            saveXml(project.getBasePath() + "/" + module.getName().substring(module.getName().indexOf(".") + 1) + "/src/main/AndroidManifest.xml", doc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveXml(String fileName, Document doc) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            DOMSource source = new DOMSource();
            source.setNode(doc);
            StreamResult result = new StreamResult();
            result.setOutputStream(new FileOutputStream(fileName));
            transformer.transform(source, result);
        } catch (TransformerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据大写字母拆分数组
     */
    private ArrayList<String> splitByUpperCase(String str) {
        ArrayList<String> rs = new ArrayList<String>();
        int index = 0;
        int len = str.length();
        for (int i = 1; i < len; i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                rs.add(str.substring(index, i));
                index = i;
            }
        }
        rs.add(str.substring(index, len));
        return rs;
    }
}
