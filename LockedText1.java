package 带锁记事本;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.io.*;
import javax.swing.undo.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

//想实现通过文件栏切换文件，但实现失败
public class LockedText1 extends JFrame implements ActionListener, DocumentListener {
	// 声明变量
	// 菜单条
	JMenuBar menuBar;
	// 菜单（用户，文件，编辑，格式，查看，帮助）
	JMenu userMenu, fileMenu, editMenu, formatMenu, viewMenu, helpMenu;
	// “用户”的菜单项
	JMenuItem userMenu_register, userMenu_changePassword;
	// “文件”的菜单项
	JMenuItem fileMenu_New, fileMenu_Open, fileMenu_Save, fileMenu_SaveAs, fileMenu_PageSetUp, fileMenu_Print,
			fileMenu_Exit;
	// “编辑”的菜单项
	JMenuItem editMenu_Undo, editMenu_Cut, editMenu_Copy, editMenu_Paste, editMenu_Delete, editMenu_Find,
			editMenu_FindNext, editMenu_Replace, editMenu_Goto, editMenu_SelectAll, editMenu_TimeDate;
	// “格式”的菜单项
	JCheckBoxMenuItem formatMenu_State, formatMenu_LineWrap;
	JMenuItem formatMenu_Font;
	// “查看”的菜单项
	JCheckBoxMenuItem viewMenu_Status;
	// “帮助”的菜单项
	JMenuItem helpMenu_ViewHelp, helpMenu_AboutNotepad;
	// 文本编辑区域
	JTextArea editArea;
	// 状态栏标签
	JLabel statusBar;
	// 右键出现菜单项
	JPopupMenu popupMenu;
	JMenuItem popupMenu_Undo, popupMenu_Cut, popupMenu_Copy, popupMenu_Paste, popupMenu_Delete, popupMenu_SelectAll;

	// 存放文本副本，用于比较，判断文本是否有改动
	String oldValue;
	// 判断文件是否保存过
	boolean isNewFile = true;
	boolean isSave = false;
	// 当前文件名
	File currentFile;

	// 补充功能
	JPanel jp;
	JButton jb1, jb2, jb3, jb4, jb5, jb6, jb7, jb8, jb9, jb10;
	JTextArea jt1, jt2, jt3, jt4, jt5, jt6, jt7, jt8, jt9, jt10;
	int i = 0;
	int j = 0;
	JButton[] container1 = new JButton[11];
	JTextArea[] container2 = new JTextArea[11];

	// 系统剪切板
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Clipboard clipBoard = toolkit.getSystemClipboard();
	// 撤销操作管理器
	UndoManager undomanager = new UndoManager();
	UndoableEditListener undoHandler = new UndoHandler();

	// 构造方法LockedText开始
	public LockedText1() {
		// 调用父类的构造方法，为窗口设标题
		super("带锁记事本1");

		// 改变系统默认字体
		Font font = new Font("Dialog", Font.PLAIN, 13);

		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, font);
			}
		}

		// 创建菜单条
		menuBar = new JMenuBar();

		// 创建用户菜单及菜单项并注册事件监听
		userMenu = new JMenu("用户(U)");
		// 设置快捷键ALT+U
		userMenu.setMnemonic('U');

		userMenu_register = new JMenuItem("注册");
		userMenu_register.addActionListener(this);

		userMenu_changePassword = new JMenuItem("更改密码");
		userMenu_changePassword.addActionListener(this);

		// 创建文件菜单及菜单项并注册事件监听
		fileMenu = new JMenu("文件(F)");
		// 设置快捷键ALT+F
		fileMenu.setMnemonic('F');

		fileMenu_New = new JMenuItem("新建(N)");
		fileMenu_New.addActionListener(this);
		// 设置快捷键Ctrl+N
		fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));

		fileMenu_Open = new JMenuItem("打开(O)...");
		fileMenu_Open.addActionListener(this);
		// 设置快捷键Ctrl+O
		fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

		fileMenu_Save = new JMenuItem("保存(S)");
		fileMenu_Save.addActionListener(this);
		// 设置快捷键Ctrl+S
		fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		fileMenu_SaveAs = new JMenuItem("另存为(A)...");
		fileMenu_SaveAs.addActionListener(this);

		fileMenu_PageSetUp = new JMenuItem("页面设置(U)...");
		fileMenu_PageSetUp.addActionListener(this);

		fileMenu_Print = new JMenuItem("打印(P)...");
		fileMenu_Print.addActionListener(this);
		// 设置快捷键Ctrl+P
		fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));

		fileMenu_Exit = new JMenuItem("退出(X)");
		fileMenu_Exit.addActionListener(this);

		// 创建编辑菜单及菜单项并注册事件监听
		editMenu = new JMenu("编辑(E)");
		// 设置快捷键ALT+E
		editMenu.setMnemonic('E');

		// 当选择编辑菜单时，设置剪切、复制、粘贴、删除等功能的可用性
		editMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e)// 取消菜单时调用
			{
				checkMenuItemEnabled();// 设置剪切、复制、粘贴、删除等功能的可用性
			}

			public void menuDeselected(MenuEvent e)// 取消选择某个菜单时调用
			{
				checkMenuItemEnabled();// 设置剪切、复制、粘贴、删除等功能的可用性
			}

			public void menuSelected(MenuEvent e)// 选择某个菜单时调用
			{
				checkMenuItemEnabled();// 设置剪切、复制、粘贴、删除等功能的可用性
			}
		});

		editMenu_Undo = new JMenuItem("撤销(U)");
		editMenu_Undo.addActionListener(this);
		editMenu_Undo.setEnabled(false);

		// 设置快捷键Ctrl+Z
		editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		editMenu_Undo.setEnabled(false);

		editMenu_Cut = new JMenuItem("剪切(T)");
		editMenu_Cut.addActionListener(this);
		// 设置快捷键Ctrl+X
		editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

		editMenu_Copy = new JMenuItem("复制(C)");
		editMenu_Copy.addActionListener(this);
		// 设置快捷键Ctrl+C
		editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));

		editMenu_Paste = new JMenuItem("粘贴(P)");
		editMenu_Paste.addActionListener(this);
		// 设置快捷键Ctrl+V
		editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));

		editMenu_Delete = new JMenuItem("删除(D)");
		editMenu_Delete.addActionListener(this);
		// 设置快捷键Delete
		editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

		editMenu_Find = new JMenuItem("查找(F)...");
		editMenu_Find.addActionListener(this);
		// 设置快捷键Ctrl+F
		editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));

		editMenu_FindNext = new JMenuItem("查找下一个(N)");
		editMenu_FindNext.addActionListener(this);
		// 设置快捷键F3
		editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

		editMenu_Replace = new JMenuItem("替换(R)...", 'R');
		editMenu_Replace.addActionListener(this);
		// 设置快捷键Ctrl+H
		editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));

		editMenu_Goto = new JMenuItem("转到(G)...", 'G');
		editMenu_Goto.addActionListener(this);
		// 设置快捷键Ctrl+G
		editMenu_Goto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));

		editMenu_SelectAll = new JMenuItem("全选", 'A');
		editMenu_SelectAll.addActionListener(this);
		// 设置快捷键Ctrl+A
		editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

		editMenu_TimeDate = new JMenuItem("时间/日期(D)", 'D');
		editMenu_TimeDate.addActionListener(this);
		// 设置快捷键F5
		editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		// 创建格式菜单及菜单项并注册事件监听
		formatMenu = new JMenu("格式(O)");
		// 设置快捷键ALT+O
		formatMenu.setMnemonic('O');

		formatMenu_LineWrap = new JCheckBoxMenuItem("自动换行(W)");
		formatMenu_LineWrap.addActionListener(this);
		// 设置快捷键ALT+W
		formatMenu_LineWrap.setMnemonic('W');
		formatMenu_LineWrap.setState(true);

		formatMenu_Font = new JMenuItem("字体(F)...");
		formatMenu_Font.addActionListener(this);

		// 创建查看菜单及菜单项并注册事件监听
		viewMenu = new JMenu("查看(V)");
		// 设置快捷键ALT+V
		viewMenu.setMnemonic('V');

		viewMenu_Status = new JCheckBoxMenuItem("状态栏(S)");
		viewMenu_Status.addActionListener(this);
		// 设置快捷键ALT+S
		viewMenu_Status.setMnemonic('S');
		viewMenu_Status.setState(true);

		// 创建帮助菜单及菜单项并注册事件监听
		helpMenu = new JMenu("帮助(H)");
		// 设置快捷键ALT+H
		helpMenu.setMnemonic('H');

		helpMenu_ViewHelp = new JMenuItem("查看帮助(H)");
		helpMenu_ViewHelp.addActionListener(this);
		// 设置快捷键F1
		helpMenu_ViewHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		helpMenu_AboutNotepad = new JMenuItem("关于记事本(A)");
		helpMenu_AboutNotepad.addActionListener(this);

		// 向窗口添加菜单条
		this.setJMenuBar(menuBar);

		// 向菜单条添加"文件"菜单及菜单项
		menuBar.add(userMenu);
		userMenu.add(userMenu_register);
		userMenu.add(userMenu_changePassword);

		// 向菜单条添加"文件"菜单及菜单项
		menuBar.add(fileMenu);
		fileMenu.add(fileMenu_New);
		fileMenu.add(fileMenu_Open);
		fileMenu.add(fileMenu_Save);
		fileMenu.add(fileMenu_SaveAs);
		fileMenu.addSeparator(); // 分隔线
		fileMenu.add(fileMenu_PageSetUp);
		fileMenu.add(fileMenu_Print);
		fileMenu.addSeparator(); // 分隔线
		fileMenu.add(fileMenu_Exit);

		// 向菜单条添加"编辑"菜单及菜单项
		menuBar.add(editMenu);
		editMenu.add(editMenu_Undo);
		editMenu.addSeparator(); // 分隔线
		editMenu.add(editMenu_Cut);
		editMenu.add(editMenu_Copy);
		editMenu.add(editMenu_Paste);
		editMenu.add(editMenu_Delete);
		editMenu.addSeparator(); // 分隔线
		editMenu.add(editMenu_Find);
		editMenu.add(editMenu_FindNext);
		editMenu.add(editMenu_Replace);
		editMenu.add(editMenu_Goto);
		editMenu.addSeparator(); // 分隔线
		editMenu.add(editMenu_SelectAll);
		editMenu.add(editMenu_TimeDate);

		// 向菜单条添加"格式"菜单及菜单项
		menuBar.add(formatMenu);
		formatMenu.add(formatMenu_LineWrap);
		formatMenu.add(formatMenu_Font);

		// 向菜单条添加"查看"菜单及菜单项
		menuBar.add(viewMenu);
		viewMenu.add(viewMenu_Status);

		// 向菜单条添加"帮助"菜单及菜单项
		menuBar.add(helpMenu);
		helpMenu.add(helpMenu_ViewHelp);
		helpMenu.addSeparator();
		helpMenu.add(helpMenu_AboutNotepad);

		// 向窗口添加文件栏
		// 构建文件栏
		jp = new JPanel();
		jp.setLayout(new GridLayout(10, 1, 0, 0));
		this.add(jp, BorderLayout.WEST);

		jb1 = new JButton();
		jb2 = new JButton();
		jb3 = new JButton();
		jb4 = new JButton();
		jb5 = new JButton();
		jb6 = new JButton();
		jb7 = new JButton();
		jb8 = new JButton();
		jb9 = new JButton();
		jb10 = new JButton();

		jb1.setVisible(false);
		jb2.setVisible(false);
		jb3.setVisible(false);
		jb4.setVisible(false);
		jb5.setVisible(false);
		jb6.setVisible(false);
		jb7.setVisible(false);
		jb8.setVisible(false);
		jb9.setVisible(false);
		jb10.setVisible(false);

		jp.add(jb1);
		jp.add(jb2);
		jp.add(jb3);
		jp.add(jb4);
		jp.add(jb5);
		jp.add(jb6);
		jp.add(jb7);
		jp.add(jb8);
		jp.add(jb9);
		jp.add(jb10);

		jt1 = new JTextArea();
		jt2 = new JTextArea();
		jt3 = new JTextArea();
		jt4 = new JTextArea();
		jt5 = new JTextArea();
		jt6 = new JTextArea();
		jt7 = new JTextArea();
		jt8 = new JTextArea();
		jt9 = new JTextArea();
		jt10 = new JTextArea();

		jt1.setVisible(false);
		jt2.setVisible(false);
		jt3.setVisible(false);
		jt4.setVisible(false);
		jt5.setVisible(false);
		jt6.setVisible(false);
		jt7.setVisible(false);
		jt8.setVisible(false);
		jt9.setVisible(false);
		jt10.setVisible(false);

		/*
		 * this.add(jt1, BorderLayout.CENTER); this.add(jt2, BorderLayout.CENTER);
		 * this.add(jt3, BorderLayout.CENTER); this.add(jt4, BorderLayout.CENTER);
		 * this.add(jt5, BorderLayout.CENTER); this.add(jt6, BorderLayout.CENTER);
		 * this.add(jt7, BorderLayout.CENTER); this.add(jt8, BorderLayout.CENTER);
		 * this.add(jt9, BorderLayout.CENTER); this.add(jt10, BorderLayout.CENTER);
		 */

		container1[0] = null;
		container1[1] = jb1;
		container1[2] = jb2;
		container1[3] = jb3;
		container1[4] = jb4;
		container1[5] = jb5;
		container1[6] = jb6;
		container1[7] = jb7;
		container1[8] = jb8;
		container1[9] = jb9;
		container1[10] = jb10;

		container2[0] = null;
		container2[1] = jt1;
		container2[2] = jt2;
		container2[3] = jt3;
		container2[4] = jt4;
		container2[5] = jt5;
		container2[6] = jt6;
		container2[7] = jt7;
		container2[8] = jt8;
		container2[9] = jt9;
		container2[10] = jt10;

		// 创建文本编辑区并添加滚动条
		editArea = new JTextArea();
		this.add(editArea, BorderLayout.CENTER);
		// editArea.setVisible(false);
		editArea.setWrapStyleWord(true);// 设置单词在一行不足容纳时换行
		editArea.setLineWrap(true);// 设置文本编辑区自动换行默认为true,即会"自动换行"
		oldValue = editArea.getText();// 获取原文本编辑区的内容

		JScrollPane scroller = new JScrollPane(editArea);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scroller, BorderLayout.CENTER);

		// 创建右键弹出菜单
		popupMenu = new JPopupMenu();
		popupMenu_Undo = new JMenuItem("撤销(U)");
		popupMenu_Cut = new JMenuItem("剪切(T)");
		popupMenu_Copy = new JMenuItem("复制(C)");
		popupMenu_Paste = new JMenuItem("粘帖(P)");
		popupMenu_Delete = new JMenuItem("删除(D)");
		popupMenu_SelectAll = new JMenuItem("全选(A)");

		popupMenu_Undo.setEnabled(false);

		// 向右键菜单添加菜单项和分隔符
		popupMenu.add(popupMenu_Undo);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_Cut);
		popupMenu.add(popupMenu_Copy);
		popupMenu.add(popupMenu_Paste);
		popupMenu.add(popupMenu_Delete);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_SelectAll);

		// 文本编辑区注册右键菜单事件
		popupMenu_Undo.addActionListener(this);
		popupMenu_Cut.addActionListener(this);
		popupMenu_Copy.addActionListener(this);
		popupMenu_Paste.addActionListener(this);
		popupMenu_Delete.addActionListener(this);
		popupMenu_SelectAll.addActionListener(this);

		// 文本编辑区注册右键菜单事件
		editArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())// 返回此鼠标事件是否为该平台的弹出菜单触发事件
				{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());// 在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
				}
				checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除等功能的可用性
				editArea.requestFocus();// 编辑区获取焦点
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())// 返回此鼠标事件是否为该平台的弹出菜单触发事件
				{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());// 在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
				}
				checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除等功能的可用性
				editArea.requestFocus();// 编辑区获取焦点
			}
		});
		// 文本编辑区注册右键菜单事件结束

		// 创建和添加状态栏
		statusBar = new JLabel("欢迎使用，此为新建文件");
		// 向窗口添加状态栏标签
		this.add(statusBar, BorderLayout.SOUTH);

		// 设置窗口在屏幕上的位置、大小和可见性
		this.setLocation(400, 125);
		this.setSize(650, 550);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 添加窗口监听器
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitWindowChoose();
			}
		});
		checkMenuItemEnabled();
		editArea.requestFocus();
	}
	// 构造方法LockedText结束

	// 设置菜单项的可用性：剪切，复制，粘帖，删除功能
	public void checkMenuItemEnabled() {
		String selectText = editArea.getSelectedText();
		if (selectText == null) {
			editMenu_Cut.setEnabled(false);
			popupMenu_Cut.setEnabled(false);
			editMenu_Copy.setEnabled(false);
			popupMenu_Copy.setEnabled(false);
			editMenu_Delete.setEnabled(false);
			popupMenu_Delete.setEnabled(false);
		} else {
			editMenu_Cut.setEnabled(true);
			popupMenu_Cut.setEnabled(true);
			editMenu_Copy.setEnabled(true);
			popupMenu_Copy.setEnabled(true);
			editMenu_Delete.setEnabled(true);
			popupMenu_Delete.setEnabled(true);
		}
		// 粘帖功能可用性判断
		Transferable contents = clipBoard.getContents(this);
		if (contents == null) {
			editMenu_Paste.setEnabled(false);
			popupMenu_Paste.setEnabled(false);
		} else {
			editMenu_Paste.setEnabled(true);
			popupMenu_Paste.setEnabled(true);
		}

		// 编辑区注册事件监听(与撤销操作有关)
		editArea.getDocument().addUndoableEditListener(undoHandler);
		editArea.getDocument().addDocumentListener(this);
	}
	// 方法checkMenuItemEnabled()结束

	// 事件响应(实现接口ActionListener的方法（只有一个）)
	public void actionPerformed(ActionEvent e) {
		// 注册
		if (e.getSource() == fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
		}
		// 注册结束

		// 更改密码
		if (e.getSource() == fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
		}
		// 更改密码结束

		// 新建
		if (e.getSource() == fileMenu_New) {
			editArea.requestFocus();
			String currentValue = editArea.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
			if (!isTextChange) {
				isSave = true;
			}
			if (isSave) {
				New();
			} else {
				int saveChoose = JOptionPane.showConfirmDialog(this, "文件尚未保存，是否保存？", "提醒",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (saveChoose == JOptionPane.YES_OPTION) {
					if (isNewFile) {
						saveAs();
						New();
					} else {
						save();
						New();
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					New();
				} else {
					statusBar.setText("新建文件失败");
					return;
				}
			}
		}
		// 新建结束

		// 打开
		else if (e.getSource() == fileMenu_Open) {
			editArea.requestFocus();
			String currentValue = editArea.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
			if (!isTextChange) {
				isSave = true;
			}
			if (isSave) {
				open();
			} else {
				int saveChoose = JOptionPane.showConfirmDialog(this, "文件尚未保存，是否保存？", "提醒",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (saveChoose == JOptionPane.YES_OPTION) {
					if (isNewFile) {
						saveAs();
						open();
					} else {
						save();
						open();
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					open();
				} else {
					return;
				}
			}
		}
		// 打开结束

		// 保存
		else if (e.getSource() == fileMenu_Save) {
			editArea.requestFocus();
			if (isNewFile) {
				saveAs();
			} else {
				save();
			}
		}
		// 保存结束

		// 另存为
		else if (e.getSource() == fileMenu_SaveAs) {
			editArea.requestFocus();
			saveAs();
		}
		// 另存为结束

		// 页面设置
		else if (e.getSource() == fileMenu_PageSetUp) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
		}
		// 页面设置结束

		// 打印
		else if (e.getSource() == fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
		}
		// 打印结束

		// 退出
		else if (e.getSource() == fileMenu_Exit) {
			editArea.requestFocus();
			String currentValue = editArea.getText();
			boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
			if (!isTextChange) {
				isSave = true;
			}
			if (isSave) {
				System.exit(0);
			} else {
				int saveChoose = JOptionPane.showConfirmDialog(this, "文件尚未保存，是否保存？", "提醒",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (saveChoose == JOptionPane.YES_OPTION) {
					if (isNewFile) {
						saveAs();
						System.exit(0);
					} else {
						save();
						System.exit(0);
					}
				} else if (saveChoose == JOptionPane.NO_OPTION) {
					System.exit(0);
				} else {
					statusBar.setText("退出失败");
					return;
				}
			}
		}
		// 退出结束

		// 撤销
		else if (e.getSource() == editMenu_Undo || e.getSource() == popupMenu_Undo) {
			editArea.requestFocus();
			if (undomanager.canUndo()) {
				try {
					undomanager.undo();
				} catch (CannotUndoException ex) {
					System.out.println("Unable to undo:" + ex);
					// ex.printStackTrace();
				}
			}
			if (!undomanager.canUndo()) {
				editMenu_Undo.setEnabled(false);
			}
		}
		// 撤销结束

		// 剪切
		else if (e.getSource() == editMenu_Cut || e.getSource() == popupMenu_Cut) {
			editArea.requestFocus();
			String text = editArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除功能的可用性
		}
		// 剪切结束

		// 复制
		else if (e.getSource() == editMenu_Copy || e.getSource() == popupMenu_Copy) {
			editArea.requestFocus();
			String text = editArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除功能的可用性
		}
		// 复制结束

		// 粘帖
		else if (e.getSource() == editMenu_Paste || e.getSource() == popupMenu_Paste) {
			editArea.requestFocus();
			Transferable contents = clipBoard.getContents(this);
			if (contents == null)
				return;
			String text = "";
			try {
				text = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception exception) {
			}
			editArea.replaceRange(text, editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled();
		}
		// 粘帖结束

		// 删除
		else if (e.getSource() == editMenu_Delete || e.getSource() == popupMenu_Delete) {
			editArea.requestFocus();
			editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
		}
		// 删除结束

		// 查找
		else if (e.getSource() == editMenu_Find) {
			editArea.requestFocus();
			find();
		}
		// 查找结束

		// 查找下一个
		else if (e.getSource() == editMenu_FindNext) {
			editArea.requestFocus();
			find();
		}
		// 查找下一个结束

		// 替换
		else if (e.getSource() == editMenu_Replace) {
			editArea.requestFocus();
			replace();
		}
		// 替换结束

		// 转到
		else if (e.getSource() == editMenu_Goto) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
		}
		// 转到结束

		// 全选
		else if (e.getSource() == editMenu_SelectAll || e.getSource() == popupMenu_SelectAll) {
			editArea.selectAll();
		}
		// 全选结束

		// 时间日期
		else if (e.getSource() == editMenu_TimeDate) {
			editArea.requestFocus();
			Calendar rightNow = Calendar.getInstance();
			Date date = rightNow.getTime();
			editArea.insert(date.toString(), editArea.getCaretPosition());
		}
		// 时间日期结束

		// 字体设置
		else if (e.getSource() == formatMenu_Font) {
			editArea.requestFocus();
			font();
		}
		// 字体设置结束

		// 自动换行(已在前面设置)
		else if (e.getSource() == formatMenu_LineWrap) {
			if (formatMenu_LineWrap.getState())
				editArea.setLineWrap(true);
			else
				editArea.setLineWrap(false);
		}
		// 自动换行结束

		// 设置状态栏可见性
		else if (e.getSource() == viewMenu_Status) {
			if (viewMenu_Status.getState())
				statusBar.setVisible(true);
			else
				statusBar.setVisible(false);
		}
		// 设置状态栏可见性结束

		// 查看帮助
		else if (e.getSource() == helpMenu_ViewHelp) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "衣带渐宽终不悔，为伊消得人憔悴", "查看帮助", JOptionPane.INFORMATION_MESSAGE);
		}
		// 查看帮助结束

		// 关于记事本
		else if (e.getSource() == helpMenu_AboutNotepad) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "此记事本只做学习用途", "关于记事本", JOptionPane.INFORMATION_MESSAGE);
		}
		// 关于记事本结束

		// 文件栏事件
		else if (e.getSource() == jb1) {
			this.remove(editArea);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt1, BorderLayout.CENTER);

			jt1.setVisible(true);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb2) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt2, BorderLayout.CENTER);

			jt2.setVisible(true);
			jt1.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb3) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt3, BorderLayout.CENTER);

			jt3.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb4) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt4, BorderLayout.CENTER);

			jt4.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb5) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt5, BorderLayout.CENTER);

			jt5.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb6) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt6, BorderLayout.CENTER);

			jt6.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb7) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt8);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt7, BorderLayout.CENTER);

			jt7.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb8) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt9);
			this.remove(jt10);
			this.add(jt8, BorderLayout.CENTER);

			jt8.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt9.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb9) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt10);
			this.add(jt9, BorderLayout.CENTER);

			jt9.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt10.setVisible(false);
			System.out.println("成功监听");
		}

		else if (e.getSource() == jb10) {
			this.remove(editArea);
			this.remove(jt1);
			this.remove(jt2);
			this.remove(jt3);
			this.remove(jt4);
			this.remove(jt5);
			this.remove(jt6);
			this.remove(jt7);
			this.remove(jt8);
			this.remove(jt9);
			this.add(jt10, BorderLayout.CENTER);

			jt10.setVisible(true);
			jt1.setVisible(false);
			jt2.setVisible(false);
			jt3.setVisible(false);
			jt4.setVisible(false);
			jt5.setVisible(false);
			jt6.setVisible(false);
			jt7.setVisible(false);
			jt8.setVisible(false);
			jt9.setVisible(false);
			System.out.println("成功监听");
		}
	}
	// 方法actionPerformed()结束

	// 实现DocumentListener接口中的方法（一共三个）(与撤销操作有关)
	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		editMenu_Undo.setEnabled(true);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		editMenu_Undo.setEnabled(true);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		editMenu_Undo.setEnabled(true);
	}

	// 实现接口UndoableEditListener的类UndoHandler(与撤销操作有关)
	class UndoHandler implements UndoableEditListener {
		@Override
		public void undoableEditHappened(UndoableEditEvent uee) {
			// TODO Auto-generated method stub
			undomanager.addEdit(uee.getEdit());
		}
	}

	// 功能实现方法
	public void New() {
		j++;
		// new LockedText();
		editArea.replaceRange("", 0, editArea.getText().length());
		statusBar.setText("新建文件" + "(" + (j) + ")");
		this.setTitle("无标题 - 记事本");
		isNewFile = true;
		undomanager.discardAllEdits();// 撤消所有的"撤消"操作
		editMenu_Undo.setEnabled(false);
		oldValue = editArea.getText();

		i++;
		New(container2[i]);
		container1[i].setVisible(true);
		container1[i].addActionListener(this);
		container1[i].setText("新建文件" + "(" + (j) + ")");
	}

	public void New(JTextArea textArea) {
		// new LockedText();
		// textArea.replaceRange("", 0, editArea.getText().length());
		// statusBar.setText("新建文件"+ "(" + j + ")");
		// this.setTitle("无标题 - 记事本");
		// isNewFile = true;
		// undomanager.discardAllEdits();// 撤消所有的"撤消"操作
		// editMenu_Undo.setEnabled(false);
		// oldValue = editArea.getText();

		System.out.println("成功调用数组");

	}

	public void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("打开文件");
		int result = fileChooser.showOpenDialog(this);
		File fileName = fileChooser.getSelectedFile();
		if (result == JFileChooser.CANCEL_OPTION) {
			statusBar.setText("您没有选择任何文件，打开失败");
			return;
		}
		if (fileName == null || fileName.getName().equals("")) {
			JOptionPane.showMessageDialog(this, "不合法的文件名", "警告", JOptionPane.ERROR_MESSAGE);
			statusBar.setText("不合法的文件名，打开失败 ");
		} else {
			FileReader fr = null;
			BufferedReader bfr = null;
			try {
				String str = null;
				fr = new FileReader(fileName);
				bfr = new BufferedReader(new FileReader(fileName));
				editArea.setText("");
				while ((str = bfr.readLine()) != null) {
					editArea.append(str + "\r\n");
				}
				isNewFile = false;
				isSave = false;
				currentFile = fileName;
				oldValue = editArea.getText();
				this.setTitle(fileName.getName() + " - 记事本");
				statusBar.setText("打开成功" + "当前打开文件：" + fileName.getAbsoluteFile());

				i++;
				open(fileName, container2[i]);
				container1[i].setVisible(true);
				container1[i].addActionListener(this);
				container1[i].setText(fileName.getName());

			} catch (IOException ioException) {
				ioException.getStackTrace();
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
				} catch (IOException ioexception) {
					ioexception.getStackTrace();
				}
				try {
					if (bfr != null) {
						bfr.close();
					}
				} catch (IOException ioexception) {
					ioexception.getStackTrace();
				}
			}
		}
	}

	public void open(File path, JTextArea textArea) {
		File fileName = path;
		FileReader fr = null;
		BufferedReader bfr = null;
		try {
			String str = null;
			fr = new FileReader(fileName);
			bfr = new BufferedReader(new FileReader(fileName));
			textArea.setText("");
			while ((str = bfr.readLine()) != null) {
				textArea.append(str + "\r\n");
			}
			isNewFile = false;
			isSave = false;
			currentFile = fileName;
			oldValue = textArea.getText();
			// this.setTitle(fileName.getName() + " - 记事本");
			// statusBar.setText("打开成功" + "当前打开文件：" + fileName.getAbsoluteFile());

			System.out.println("成功调用数组");
			System.out.println(textArea.getText());

		} catch (IOException ioException) {
			ioException.getStackTrace();
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException ioexception) {
				ioexception.getStackTrace();
			}
			try {
				if (bfr != null) {
					bfr.close();
				}
			} catch (IOException ioexception) {
				ioexception.getStackTrace();
			}
		}
	}

	public void save() {
		FileWriter fw = null;
		BufferedWriter bfw = null;
		try {
			fw = new FileWriter(currentFile);
			bfw = new BufferedWriter(fw);
			bfw.write(editArea.getText(), 0, editArea.getText().length());
			isSave = true;
			bfw.flush();
			statusBar.setText("保存成功" + " 当前文件路径：" + currentFile.getAbsoluteFile());
		} catch (IOException ioException) {
			ioException.getStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ioException) {
				ioException.getStackTrace();
			}
			try {
				if (bfw != null) {
					bfw.close();
				}
			} catch (IOException ioException) {
				ioException.getStackTrace();
			}
		}
	}

	public void saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("另存为");
		int result = fileChooser.showSaveDialog(this);
		File saveAsFileName = fileChooser.getSelectedFile();
		if (result == JFileChooser.CANCEL_OPTION) {
			statusBar.setText("您没有选择任何文件，另存为失败");
			return;
		}
		if (saveAsFileName == null || saveAsFileName.getName().equals("")) {
			JOptionPane.showMessageDialog(this, "不合法的文件名", "警告", JOptionPane.ERROR_MESSAGE);
			statusBar.setText("不合法的文件名，另存为失败");
		} else {
			FileWriter fw = null;
			BufferedWriter bfw = null;
			try {
				String str = null;
				fw = new FileWriter(saveAsFileName);
				bfw = new BufferedWriter(fw);
				// bfw.write(editArea.getText(), 0, editArea.getText().length());
				bfw.write(editArea.getText());
				bfw.flush();
				isNewFile = false;
				isSave = true;
				currentFile = saveAsFileName;
				oldValue = editArea.getText();
				this.setTitle(saveAsFileName.getName() + " - 记事本");
				statusBar.setText("另存为成功" + " 当前文件路径：" + saveAsFileName.getAbsoluteFile());
			} catch (IOException ioexception) {
				ioexception.getStackTrace();
			} finally {
				try {
					if (fw != null) {
						fw.close();
					}
				} catch (IOException ioException) {
					ioException.getStackTrace();
				}
				try {
					if (bfw != null) {
						bfw.close();
					}
				} catch (IOException ioexception) {
					ioexception.getStackTrace();
				}
			}
		}
	}

	public void font() {
		editArea.requestFocus();
		JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
	}

	public void find() {
		// 创建"查找"对话框的界面
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();
		jp4.setBorder(BorderFactory.createTitledBorder("方向"));
		JLabel jl = new JLabel("查找内容(N)：");
		JTextField jt = new JTextField(20);
		JButton jbt1 = new JButton("查找下一个(F)：");
		JButton jbt2 = new JButton("取消");
		JCheckBox jcb = new JCheckBox("区分大小写(C)");
		JRadioButton jrb1 = new JRadioButton("向上(U)");
		JRadioButton jrb2 = new JRadioButton("向下(D)");
		ButtonGroup btg = new ButtonGroup();
		JDialog findDialog = new JDialog(this, "查找", false);
		Container container = findDialog.getContentPane();
		jrb2.setSelected(true);
		btg.add(jrb1);
		btg.add(jrb2);

		container.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp2.setLayout(new GridLayout(2, 2));
		container.add(jp1);
		container.add(jp2);
		container.add(jp3);
		jp1.add(jl);
		jp1.add(jt);
		jp2.add(jbt1);
		jp2.add(jbt2);
		jp3.add(jcb);
		jp3.add(jp4);
		jp4.add(jrb1);
		jp4.add(jrb2);

		findDialog.setSize(460, 180);
		findDialog.setResizable(false);
		findDialog.setLocation(500, 320);
		findDialog.setVisible(true);

		// 事件监听

		// 取消按钮事件处理
		jbt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findDialog.dispose();
			}
		});

		// "查找下一个"按钮监听
		jbt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // "区分大小写(C)"的JCheckBox是否被选中
				int k = 0, m = 0;
				final String str1, str2, str3, str4, strA, strB;
				str1 = editArea.getText();
				str2 = jt.getText();
				str3 = str1.toUpperCase();
				str4 = str2.toUpperCase();
				if (jcb.isSelected())// 区分大小写
				{
					strA = str1;
					strB = str2;
				} else// 不区分大小写,此时把所选内容全部化成大写(或小写)，以便于查找
				{
					strA = str3;
					strB = str4;
				}
				if (jrb1.isSelected()) { // k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
					if (editArea.getSelectedText() == null)
						k = strA.lastIndexOf(strB, editArea.getCaretPosition() - 1);
					else
						k = strA.lastIndexOf(strB, editArea.getCaretPosition() - jt.getText().length() - 1);
					if (k > -1) { // String strData=strA.subString(k,strB.getText().length()+1);
						editArea.setCaretPosition(k);
						editArea.select(k, k + strB.length());
					} else {
						JOptionPane.showMessageDialog(null, "找不到您查找的内容！", "查找", JOptionPane.INFORMATION_MESSAGE);
					}
				} else if (jrb2.isSelected()) {
					if (editArea.getSelectedText() == null)
						k = strA.indexOf(strB, editArea.getCaretPosition() + 1);
					else
						k = strA.indexOf(strB, editArea.getCaretPosition() - jt.getText().length() + 1);
					if (k > -1) { // String strData=strA.subString(k,strB.getText().length()+1);
						editArea.setCaretPosition(k);
						editArea.select(k, k + strB.length());
					} else {
						JOptionPane.showMessageDialog(null, "找不到您查找的内容！", "查找", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});

	}

	public void replace() {
		editArea.requestFocus();
		JOptionPane.showMessageDialog(this, "此功能尚未实现", "提示", JOptionPane.WARNING_MESSAGE);
	}

	public void exitWindowChoose() {
		editArea.requestFocus();
		String currentValue = editArea.getText();
		boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
		if (!isTextChange) {
			isSave = true;
		}
		if (isSave) {
			System.exit(0);
		} else {
			int saveChoose = JOptionPane.showConfirmDialog(this, "文件尚未保存，是否保存？", "提醒",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (saveChoose == JOptionPane.YES_OPTION) {
				if (isNewFile) {
					saveAs();
					System.exit(0);
				} else {
					save();
					System.exit(0);
				}
			} else if (saveChoose == JOptionPane.NO_OPTION) {
				System.exit(0);
			} else {
				statusBar.setText("退出失败");
				return;
			}
		}
	}

	// 主方法
	public static void main(String args[]) {
		new LockedText1();
	}
}
