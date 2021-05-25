package com.github.duoluo9.BaseLibraryPlugin;

import javax.swing.*;
import java.awt.event.*;

public class BaseTitleBarActivityDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JRadioButton radioButton1;

    private DialogCallBack mCallBack;

    public BaseTitleBarActivityDialog(DialogCallBack callBack) {
        this.mCallBack = callBack;
        setTitle("BaseTitleBarActivityDialog");
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
        void ok(String pageName,  boolean isKotlin);
    }
}
