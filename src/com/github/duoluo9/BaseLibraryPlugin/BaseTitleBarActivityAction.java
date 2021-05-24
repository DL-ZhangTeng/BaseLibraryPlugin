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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTitleBarActivityAction extends AnAction {


    private Project project;
    private Module module;
    //包名
    private String packageName = "";
    private String pageName;
    private boolean needActivity;
    private boolean needFragment;
    private boolean isKotlin;


    private enum CodeType {
        Activity, Fragment, COMPONENT, Contract, Presenter, MODEL, MODULE, Activity_LAYOUT, Fragment_LAYOUT
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
        MyDialog myDialog = new MyDialog((pageName, needActivity, needFragment, isKotlin) -> {
            BaseTitleBarActivityAction.this.pageName = pageName;
            BaseTitleBarActivityAction.this.needActivity = needActivity;
            BaseTitleBarActivityAction.this.needFragment = needFragment;
            BaseTitleBarActivityAction.this.isKotlin = isKotlin;
            createClassFiles();
        });
        myDialog.setVisible(true);

    }

    /**
     * 生成类文件
     */
    private void createClassFiles() {
        if (needActivity) {
            createClassFile(CodeType.Activity);
            createClassFile(CodeType.Activity_LAYOUT);
        }
        if (needFragment) {
            createClassFile(CodeType.Fragment);
            createClassFile(CodeType.Fragment_LAYOUT);
        }
        createClassFile(CodeType.Contract);
        createClassFile(CodeType.Presenter);
        createClassFile(CodeType.COMPONENT);
        createClassFile(CodeType.MODEL);
        createClassFile(CodeType.MODULE);
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
                fileName = isKotlin ? "ArmsActivity.kt.ftl" : "ArmsActivity.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "mvp/ui/activity", pageName + "Activity.kt");
                else
                    writeToFile(content, appPath + "mvp/ui/activity", pageName + "Activity.java");

                break;
            case Fragment:
                fileName = isKotlin ? "ArmsFragment.kt.ftl" : "ArmsFragment.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "mvp/ui/fragment", pageName + "Fragment.kt");
                else
                    writeToFile(content, appPath + "mvp/ui/fragment", pageName + "Fragment.java");
                break;
            case Contract:
                fileName = isKotlin ? "ArmsContract.kt.ftl" : "ArmsContract.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "mvp/contract", pageName + "Contract.kt");
                else
                    writeToFile(content, appPath + "mvp/contract", pageName + "Contract.java");
                break;
            case Presenter:
                fileName = isKotlin ? "ArmsPresenter.kt.ftl" : "ArmsPresenter.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateScope(content);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "mvp/presenter", pageName + "Presenter.kt");
                else
                    writeToFile(content, appPath + "mvp/presenter", pageName + "Presenter.java");
                break;
            case MODEL:
                fileName = isKotlin ? "ArmsModel.kt.ftl" : "ArmsModel.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateScope(content);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "mvp/model", pageName + "Model.kt");
                else
                    writeToFile(content, appPath + "mvp/model", pageName + "Model.java");
                break;
            case COMPONENT:
                fileName = isKotlin ? "ArmsComponent.kt.ftl" : "ArmsComponent.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateScope(content);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "di/component", pageName + "Component.kt");
                else
                    writeToFile(content, appPath + "di/component", pageName + "Component.java");
                break;
            case MODULE:
                fileName = isKotlin ? "ArmsModule.kt.ftl" : "ArmsModule.java.ftl";
                content = ReadTemplateFile(fileName);
                content = dealTemplateScope(content);
                content = dealTemplateContent(content);
                if (isKotlin)
                    writeToFile(content, appPath + "di/module", pageName + "Module.kt");
                else
                    writeToFile(content, appPath + "di/module", pageName + "Module.java");
                break;
            case Activity_LAYOUT:
                fileName = "simple.xml.ftl";
                content = ReadTemplateFile(fileName);
                writeToFile(content, getAppResPath(), "activity_" + pageName.toLowerCase() + ".xml");
                break;
            case Fragment_LAYOUT:
                fileName = "simple.xml.ftl";
                content = ReadTemplateFile(fileName);
                writeToFile(content, getAppResPath(), "fragment_" + pageName.toLowerCase() + ".xml");
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
            content = content.replace("${activityLayoutName}", "activity_" + pageName.toLowerCase());
        }
        if (content.contains("${fragmentLayoutName}")) {
            content = content.replace("${fragmentLayoutName}", "fragment_" + pageName.toLowerCase());
        }
        return content;
    }

    /**
     * 替换模板中scope字符
     *
     * @param content
     * @return
     */
    private String dealTemplateScope(String content) {
        if (content.contains("${scope}")) {
            if (needActivity) {
                content = content.replace("${scope}", "@ActivityScope");
            } else {
                content = content.replace("${scope}", "@FragmentScope");
            }
        }
        if (content.contains("${inject}")) {
            if (needActivity && needFragment) {
                if (isKotlin) {
                    content = content.replace("${inject}", "fun inject(activity: " + pageName + "Activity?)"
                            + "\n\n" +
                            "    fun inject(fragment: " + pageName + "Fragment?)");
                } else {
                    content = content.replace("${inject}", "void inject(" + pageName + "Activity activity);"
                            + "\n\n" +
                            "    void inject(" + pageName + "Fragment fragment);");
                }
            } else if (needActivity) {
                if (isKotlin){
                    content = content.replace("${inject}", "fun inject(activity: " + pageName + "Activity?)");
                }else {
                    content = content.replace("${inject}", "void inject(" + pageName + "Activity activity);");
                }
            } else if (needFragment) {
                if (isKotlin){
                    content = content.replace("${inject}", "fun inject(fragment: " + pageName + "Fragment?)");
                }else {
                    content = content.replace("${inject}", "void inject(" + pageName + "Fragment fragment);");
                }
            }
        }
        if (content.contains("${scopeImport}") || content.contains("${scopeLessImport}")) {
            if (needActivity && needFragment) {
                String scopeLessImport = "import com.jess.arms.di.scope.ActivityScope";
                if (!isKotlin)
                    scopeLessImport = scopeLessImport + ";";
                String scopeImport = scopeLessImport + "\n" +
                        "import " + packageName + ".mvp.ui.activity." + pageName + "Activity";
                if (!isKotlin)
                    scopeImport = scopeImport + ";";
                scopeImport = scopeImport + "\n" +
                        "import " + packageName + ".mvp.ui.fragment." + pageName + "Fragment";
                if (!isKotlin)
                    scopeImport = scopeImport + ";";
                content = content.replace("${scopeImport}", scopeImport);
                content = content.replace("${scopeLessImport}", scopeLessImport);
            } else if (needActivity) {
                String scopeLessImport = "import com.jess.arms.di.scope.ActivityScope";
                if (!isKotlin)
                    scopeLessImport = scopeLessImport + ";";
                String scopeImport = scopeLessImport + "\n" +
                        "import " + packageName + ".mvp.ui.activity." + pageName + "Activity";
                if (!isKotlin)
                    scopeImport = scopeImport + ";";
                content = content.replace("${scopeImport}", scopeImport);
                content = content.replace("${scopeLessImport}", scopeLessImport);
            } else if (needFragment) {
                String scopeLessImport = "import com.jess.arms.di.scope.FragmentScope";
                if (!isKotlin)
                    scopeLessImport = scopeLessImport + ";";
                String scopeImport = scopeLessImport + "\n" +
                        "import " + packageName + ".mvp.ui.fragment." + pageName + "Fragment";
                if (!isKotlin)
                    scopeImport = scopeImport + ";";
                content = content.replace("${scopeImport}", scopeImport);
                content = content.replace("${scopeLessImport}", scopeLessImport);
            }
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
        in = this.getClass().getResourceAsStream("/com/github/duoluo9/MVPArmsPlugin/Template/" + fileName);
        String content = "";
        try {
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
}
