工欲善其事必先利其器，使用插件能够极大的提高开发效率。AndroidStudio基于IntelliJ平台，因此，开发AndroidStudio插件其本质只是开发IntelliJ平台的插件。
下文以模板创建BaseActivity为例 [MVPArmsPlugin](https://github.com/DL-ZhangTeng/MVPArmsPlugin) &  [BaseLibraryPlugin](https://github.com/DL-ZhangTeng/BaseLibraryPlugin) ：
# 1、下载IntelliJ IDEA
IntelliJ IDEA集成了插件开发环境，下载后可以直接拿来开发插件。IntelliJ IDEA下载地址如下：
 [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)
 
# 2、创建项目
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526153343551.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526153434379.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)

# 3、创建Action
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526153539456.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)
## 3.1、创建BaseActivityAction
```java
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

public class BaseActivityAction extends AnAction {


    private Project project;
    private Module module;
    //包名
    private String packageName = "";
    private String pageName;
    private boolean isKotlin;

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
        BaseActivityDialog myDialog = new BaseActivityDialog((pageName, isKotlin) -> {
            BaseActivityAction.this.pageName = pageName;
            BaseActivityAction.this.isKotlin = isKotlin;
            createClassFile();
        });
        myDialog.setVisible(true);

    }

    /**
     * 生成mvp框架代码
     */
    private void createClassFile() {
        String appPath = getAppPath();
        String fileName = isKotlin ? "BaseActivity.kt.ftl" : "BaseActivity.java.ftl";
        String content = ReadTemplateFile(fileName);
        content = dealTemplateContent(content);
        if (isKotlin)
            writeToFile(content, appPath + "activity", pageName + "Activity.kt");
        else
            writeToFile(content, appPath + "activity", pageName + "Activity.java");

        String layoutFileName = "simple.xml.ftl";
        String layoutContent = ReadTemplateFile(layoutFileName);
        writeToFile(layoutContent, getAppResPath(), "activity_" + pageName.toLowerCase() + ".xml");
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
        InputStream in = this.getClass().getResourceAsStream("/com/github/duoluo9/BaseLibraryPlugin/Template/" + fileName);
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
}

```
## 3.2、创建BaseLibraryGroup
添加菜单组（group）必须有一个DefaultActionGroup

```java
package com.github.duoluo9.BaseLibraryPlugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;

public class BaseLibraryGroup extends DefaultActionGroup {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }
}

```
## 3.3、创建BaseActivityDialog
可以使用拖曳的方式创建视图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526155142180.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)

```java
package com.github.duoluo9.BaseLibraryPlugin;

import javax.swing.*;
import java.awt.event.*;

public class BaseActivityDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JRadioButton radioButton1;

    private DialogCallBack mCallBack;

    public BaseActivityDialog(DialogCallBack callBack) {
        this.mCallBack = callBack;
        setTitle("BaseActivityDialog");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(480, 150);
        setLocationRelativeTo(null);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (null != mCallBack) {
            mCallBack.ok(textField1.getText().trim(), radioButton1.isSelected());
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public interface DialogCallBack {
        void ok(String pageName, boolean isKotlin);
    }
}

```
# 4、修改plugin.xml
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526154035584.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)

```xml
<idea-plugin>
    <id>com.github.duoluo9.BaseLibraryPlugin.plugin.id</id>
    <name>BaseLibraryPlugin</name>
    <version>1.0</version>
    <vendor email="763263311@qq.com" url="https://github.com/duoluo9">duoluo9</vendor>

    <description><![CDATA[BaseLibraryPlugin<br>]]></description>

    <change-notes><![CDATA[
      1.0<br>
    ]]>
    </change-notes>

    <!-- please see https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html for description -->
    <idea-version since-build="173.0"/>

    <!-- please see https://plugins.jetbrains.com/docs/intellij/plugin-compatibility.html
         on how to target different products -->
    <depends>com.intellij.modules.platform</depends>

    <extensions defaultExtensionNs="com.intellij">
        <!-- Add your extensions here -->
    </extensions>

    <actions>
        <group id="com.github.duoluo9.BaseLibrary" popup="true" searchable="true"
               class="com.github.duoluo9.BaseLibraryPlugin.BaseLibraryGroup" text="BaseLibrary"
               description="BaseLibrary中的类生成工具">
            <action id="com.github.duoluo9.BaseActivityAction"
                    class="com.github.duoluo9.BaseLibraryPlugin.BaseActivityAction"
                    text="BaseActivity"
                    description="BaseLibrary中BaseActivity的类生成工具">
            </action>
            <action id="com.github.duoluo9.BaseMvpActivityAction"
                    class="com.github.duoluo9.BaseLibraryPlugin.BaseMvpActivityAction"
                    text="BaseMvpActivity"
                    description="BaseLibrary中BaseMvpActivity的类生成工具">
            </action>
            <action id="com.github.duoluo9.BaseTitleBarActivityAction"
                    class="com.github.duoluo9.BaseLibraryPlugin.BaseTitleBarActivityAction"
                    text="BaseTitleBarActivity"
                    description="BaseLibrary中BaseTitleBarActivity的类生成工具">
            </action>
            <add-to-group group-id="NewGroup" anchor="first"/>
        </group>
    </actions>

</idea-plugin>
```

# 5、构建本地jar
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526154246398.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)
# 6、安装插件BaseLibraryPlugin.jar
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526154354764.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)

# 7、使用插件快速生成BaseActivity代码
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526154504657.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526154544287.png)
# 8、常见问题
### 8.1、插件无法在AndroidStudio中使用，可以安装但是没有创建的菜单（因为Idea插件默认JDK11而AndroidStudio默认JDK1.8）
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526155517701.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)
### 8.2、无法使用Idea的run按钮调试插件
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210526155950498.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2R1b2x1bzk=,size_16,color_FFFFFF,t_70)







