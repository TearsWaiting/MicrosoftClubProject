package �������±�;

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

//��ʵ��ͨ���ļ����л��ļ�����ʵ��ʧ��
public class LockedText1 extends JFrame implements ActionListener, DocumentListener {
	// ��������
	// �˵���
	JMenuBar menuBar;
	// �˵����û����ļ����༭����ʽ���鿴��������
	JMenu userMenu, fileMenu, editMenu, formatMenu, viewMenu, helpMenu;
	// ���û����Ĳ˵���
	JMenuItem userMenu_register, userMenu_changePassword;
	// ���ļ����Ĳ˵���
	JMenuItem fileMenu_New, fileMenu_Open, fileMenu_Save, fileMenu_SaveAs, fileMenu_PageSetUp, fileMenu_Print,
			fileMenu_Exit;
	// ���༭���Ĳ˵���
	JMenuItem editMenu_Undo, editMenu_Cut, editMenu_Copy, editMenu_Paste, editMenu_Delete, editMenu_Find,
			editMenu_FindNext, editMenu_Replace, editMenu_Goto, editMenu_SelectAll, editMenu_TimeDate;
	// ����ʽ���Ĳ˵���
	JCheckBoxMenuItem formatMenu_State, formatMenu_LineWrap;
	JMenuItem formatMenu_Font;
	// ���鿴���Ĳ˵���
	JCheckBoxMenuItem viewMenu_Status;
	// ���������Ĳ˵���
	JMenuItem helpMenu_ViewHelp, helpMenu_AboutNotepad;
	// �ı��༭����
	JTextArea editArea;
	// ״̬����ǩ
	JLabel statusBar;
	// �Ҽ����ֲ˵���
	JPopupMenu popupMenu;
	JMenuItem popupMenu_Undo, popupMenu_Cut, popupMenu_Copy, popupMenu_Paste, popupMenu_Delete, popupMenu_SelectAll;

	// ����ı����������ڱȽϣ��ж��ı��Ƿ��иĶ�
	String oldValue;
	// �ж��ļ��Ƿ񱣴��
	boolean isNewFile = true;
	boolean isSave = false;
	// ��ǰ�ļ���
	File currentFile;

	// ���书��
	JPanel jp;
	JButton jb1, jb2, jb3, jb4, jb5, jb6, jb7, jb8, jb9, jb10;
	JTextArea jt1, jt2, jt3, jt4, jt5, jt6, jt7, jt8, jt9, jt10;
	int i = 0;
	int j = 0;
	JButton[] container1 = new JButton[11];
	JTextArea[] container2 = new JTextArea[11];

	// ϵͳ���а�
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Clipboard clipBoard = toolkit.getSystemClipboard();
	// ��������������
	UndoManager undomanager = new UndoManager();
	UndoableEditListener undoHandler = new UndoHandler();

	// ���췽��LockedText��ʼ
	public LockedText1() {
		// ���ø���Ĺ��췽����Ϊ���������
		super("�������±�1");

		// �ı�ϵͳĬ������
		Font font = new Font("Dialog", Font.PLAIN, 13);

		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, font);
			}
		}

		// �����˵���
		menuBar = new JMenuBar();

		// �����û��˵����˵��ע���¼�����
		userMenu = new JMenu("�û�(U)");
		// ���ÿ�ݼ�ALT+U
		userMenu.setMnemonic('U');

		userMenu_register = new JMenuItem("ע��");
		userMenu_register.addActionListener(this);

		userMenu_changePassword = new JMenuItem("��������");
		userMenu_changePassword.addActionListener(this);

		// �����ļ��˵����˵��ע���¼�����
		fileMenu = new JMenu("�ļ�(F)");
		// ���ÿ�ݼ�ALT+F
		fileMenu.setMnemonic('F');

		fileMenu_New = new JMenuItem("�½�(N)");
		fileMenu_New.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+N
		fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));

		fileMenu_Open = new JMenuItem("��(O)...");
		fileMenu_Open.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+O
		fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

		fileMenu_Save = new JMenuItem("����(S)");
		fileMenu_Save.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+S
		fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		fileMenu_SaveAs = new JMenuItem("���Ϊ(A)...");
		fileMenu_SaveAs.addActionListener(this);

		fileMenu_PageSetUp = new JMenuItem("ҳ������(U)...");
		fileMenu_PageSetUp.addActionListener(this);

		fileMenu_Print = new JMenuItem("��ӡ(P)...");
		fileMenu_Print.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+P
		fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));

		fileMenu_Exit = new JMenuItem("�˳�(X)");
		fileMenu_Exit.addActionListener(this);

		// �����༭�˵����˵��ע���¼�����
		editMenu = new JMenu("�༭(E)");
		// ���ÿ�ݼ�ALT+E
		editMenu.setMnemonic('E');

		// ��ѡ��༭�˵�ʱ�����ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
		editMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e)// ȡ���˵�ʱ����
			{
				checkMenuItemEnabled();// ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
			}

			public void menuDeselected(MenuEvent e)// ȡ��ѡ��ĳ���˵�ʱ����
			{
				checkMenuItemEnabled();// ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
			}

			public void menuSelected(MenuEvent e)// ѡ��ĳ���˵�ʱ����
			{
				checkMenuItemEnabled();// ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
			}
		});

		editMenu_Undo = new JMenuItem("����(U)");
		editMenu_Undo.addActionListener(this);
		editMenu_Undo.setEnabled(false);

		// ���ÿ�ݼ�Ctrl+Z
		editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		editMenu_Undo.setEnabled(false);

		editMenu_Cut = new JMenuItem("����(T)");
		editMenu_Cut.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+X
		editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

		editMenu_Copy = new JMenuItem("����(C)");
		editMenu_Copy.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+C
		editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));

		editMenu_Paste = new JMenuItem("ճ��(P)");
		editMenu_Paste.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+V
		editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));

		editMenu_Delete = new JMenuItem("ɾ��(D)");
		editMenu_Delete.addActionListener(this);
		// ���ÿ�ݼ�Delete
		editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

		editMenu_Find = new JMenuItem("����(F)...");
		editMenu_Find.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+F
		editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));

		editMenu_FindNext = new JMenuItem("������һ��(N)");
		editMenu_FindNext.addActionListener(this);
		// ���ÿ�ݼ�F3
		editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

		editMenu_Replace = new JMenuItem("�滻(R)...", 'R');
		editMenu_Replace.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+H
		editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));

		editMenu_Goto = new JMenuItem("ת��(G)...", 'G');
		editMenu_Goto.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+G
		editMenu_Goto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));

		editMenu_SelectAll = new JMenuItem("ȫѡ", 'A');
		editMenu_SelectAll.addActionListener(this);
		// ���ÿ�ݼ�Ctrl+A
		editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

		editMenu_TimeDate = new JMenuItem("ʱ��/����(D)", 'D');
		editMenu_TimeDate.addActionListener(this);
		// ���ÿ�ݼ�F5
		editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		// ������ʽ�˵����˵��ע���¼�����
		formatMenu = new JMenu("��ʽ(O)");
		// ���ÿ�ݼ�ALT+O
		formatMenu.setMnemonic('O');

		formatMenu_LineWrap = new JCheckBoxMenuItem("�Զ�����(W)");
		formatMenu_LineWrap.addActionListener(this);
		// ���ÿ�ݼ�ALT+W
		formatMenu_LineWrap.setMnemonic('W');
		formatMenu_LineWrap.setState(true);

		formatMenu_Font = new JMenuItem("����(F)...");
		formatMenu_Font.addActionListener(this);

		// �����鿴�˵����˵��ע���¼�����
		viewMenu = new JMenu("�鿴(V)");
		// ���ÿ�ݼ�ALT+V
		viewMenu.setMnemonic('V');

		viewMenu_Status = new JCheckBoxMenuItem("״̬��(S)");
		viewMenu_Status.addActionListener(this);
		// ���ÿ�ݼ�ALT+S
		viewMenu_Status.setMnemonic('S');
		viewMenu_Status.setState(true);

		// ���������˵����˵��ע���¼�����
		helpMenu = new JMenu("����(H)");
		// ���ÿ�ݼ�ALT+H
		helpMenu.setMnemonic('H');

		helpMenu_ViewHelp = new JMenuItem("�鿴����(H)");
		helpMenu_ViewHelp.addActionListener(this);
		// ���ÿ�ݼ�F1
		helpMenu_ViewHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		helpMenu_AboutNotepad = new JMenuItem("���ڼ��±�(A)");
		helpMenu_AboutNotepad.addActionListener(this);

		// �򴰿���Ӳ˵���
		this.setJMenuBar(menuBar);

		// ��˵������"�ļ�"�˵����˵���
		menuBar.add(userMenu);
		userMenu.add(userMenu_register);
		userMenu.add(userMenu_changePassword);

		// ��˵������"�ļ�"�˵����˵���
		menuBar.add(fileMenu);
		fileMenu.add(fileMenu_New);
		fileMenu.add(fileMenu_Open);
		fileMenu.add(fileMenu_Save);
		fileMenu.add(fileMenu_SaveAs);
		fileMenu.addSeparator(); // �ָ���
		fileMenu.add(fileMenu_PageSetUp);
		fileMenu.add(fileMenu_Print);
		fileMenu.addSeparator(); // �ָ���
		fileMenu.add(fileMenu_Exit);

		// ��˵������"�༭"�˵����˵���
		menuBar.add(editMenu);
		editMenu.add(editMenu_Undo);
		editMenu.addSeparator(); // �ָ���
		editMenu.add(editMenu_Cut);
		editMenu.add(editMenu_Copy);
		editMenu.add(editMenu_Paste);
		editMenu.add(editMenu_Delete);
		editMenu.addSeparator(); // �ָ���
		editMenu.add(editMenu_Find);
		editMenu.add(editMenu_FindNext);
		editMenu.add(editMenu_Replace);
		editMenu.add(editMenu_Goto);
		editMenu.addSeparator(); // �ָ���
		editMenu.add(editMenu_SelectAll);
		editMenu.add(editMenu_TimeDate);

		// ��˵������"��ʽ"�˵����˵���
		menuBar.add(formatMenu);
		formatMenu.add(formatMenu_LineWrap);
		formatMenu.add(formatMenu_Font);

		// ��˵������"�鿴"�˵����˵���
		menuBar.add(viewMenu);
		viewMenu.add(viewMenu_Status);

		// ��˵������"����"�˵����˵���
		menuBar.add(helpMenu);
		helpMenu.add(helpMenu_ViewHelp);
		helpMenu.addSeparator();
		helpMenu.add(helpMenu_AboutNotepad);

		// �򴰿�����ļ���
		// �����ļ���
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

		// �����ı��༭������ӹ�����
		editArea = new JTextArea();
		this.add(editArea, BorderLayout.CENTER);
		// editArea.setVisible(false);
		editArea.setWrapStyleWord(true);// ���õ�����һ�в�������ʱ����
		editArea.setLineWrap(true);// �����ı��༭���Զ�����Ĭ��Ϊtrue,����"�Զ�����"
		oldValue = editArea.getText();// ��ȡԭ�ı��༭��������

		JScrollPane scroller = new JScrollPane(editArea);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scroller, BorderLayout.CENTER);

		// �����Ҽ������˵�
		popupMenu = new JPopupMenu();
		popupMenu_Undo = new JMenuItem("����(U)");
		popupMenu_Cut = new JMenuItem("����(T)");
		popupMenu_Copy = new JMenuItem("����(C)");
		popupMenu_Paste = new JMenuItem("ճ��(P)");
		popupMenu_Delete = new JMenuItem("ɾ��(D)");
		popupMenu_SelectAll = new JMenuItem("ȫѡ(A)");

		popupMenu_Undo.setEnabled(false);

		// ���Ҽ��˵���Ӳ˵���ͷָ���
		popupMenu.add(popupMenu_Undo);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_Cut);
		popupMenu.add(popupMenu_Copy);
		popupMenu.add(popupMenu_Paste);
		popupMenu.add(popupMenu_Delete);
		popupMenu.addSeparator();
		popupMenu.add(popupMenu_SelectAll);

		// �ı��༭��ע���Ҽ��˵��¼�
		popupMenu_Undo.addActionListener(this);
		popupMenu_Cut.addActionListener(this);
		popupMenu_Copy.addActionListener(this);
		popupMenu_Paste.addActionListener(this);
		popupMenu_Delete.addActionListener(this);
		popupMenu_SelectAll.addActionListener(this);

		// �ı��༭��ע���Ҽ��˵��¼�
		editArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())// ���ش�����¼��Ƿ�Ϊ��ƽ̨�ĵ����˵������¼�
				{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());// ����������ߵ�����ռ��е�λ�� X��Y ��ʾ�����˵�
				}
				checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ���ȹ��ܵĿ�����
				editArea.requestFocus();// �༭����ȡ����
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())// ���ش�����¼��Ƿ�Ϊ��ƽ̨�ĵ����˵������¼�
				{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());// ����������ߵ�����ռ��е�λ�� X��Y ��ʾ�����˵�
				}
				checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ���ȹ��ܵĿ�����
				editArea.requestFocus();// �༭����ȡ����
			}
		});
		// �ı��༭��ע���Ҽ��˵��¼�����

		// ���������״̬��
		statusBar = new JLabel("��ӭʹ�ã���Ϊ�½��ļ�");
		// �򴰿����״̬����ǩ
		this.add(statusBar, BorderLayout.SOUTH);

		// ���ô�������Ļ�ϵ�λ�á���С�Ϳɼ���
		this.setLocation(400, 125);
		this.setSize(650, 550);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ��Ӵ��ڼ�����
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitWindowChoose();
			}
		});
		checkMenuItemEnabled();
		editArea.requestFocus();
	}
	// ���췽��LockedText����

	// ���ò˵���Ŀ����ԣ����У����ƣ�ճ����ɾ������
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
		// ճ�����ܿ������ж�
		Transferable contents = clipBoard.getContents(this);
		if (contents == null) {
			editMenu_Paste.setEnabled(false);
			popupMenu_Paste.setEnabled(false);
		} else {
			editMenu_Paste.setEnabled(true);
			popupMenu_Paste.setEnabled(true);
		}

		// �༭��ע���¼�����(�볷�������й�)
		editArea.getDocument().addUndoableEditListener(undoHandler);
		editArea.getDocument().addDocumentListener(this);
	}
	// ����checkMenuItemEnabled()����

	// �¼���Ӧ(ʵ�ֽӿ�ActionListener�ķ�����ֻ��һ����)
	public void actionPerformed(ActionEvent e) {
		// ע��
		if (e.getSource() == fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
		}
		// ע�����

		// ��������
		if (e.getSource() == fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
		}
		// �����������

		// �½�
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
				int saveChoose = JOptionPane.showConfirmDialog(this, "�ļ���δ���棬�Ƿ񱣴棿", "����",
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
					statusBar.setText("�½��ļ�ʧ��");
					return;
				}
			}
		}
		// �½�����

		// ��
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
				int saveChoose = JOptionPane.showConfirmDialog(this, "�ļ���δ���棬�Ƿ񱣴棿", "����",
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
		// �򿪽���

		// ����
		else if (e.getSource() == fileMenu_Save) {
			editArea.requestFocus();
			if (isNewFile) {
				saveAs();
			} else {
				save();
			}
		}
		// �������

		// ���Ϊ
		else if (e.getSource() == fileMenu_SaveAs) {
			editArea.requestFocus();
			saveAs();
		}
		// ���Ϊ����

		// ҳ������
		else if (e.getSource() == fileMenu_PageSetUp) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
		}
		// ҳ�����ý���

		// ��ӡ
		else if (e.getSource() == fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
		}
		// ��ӡ����

		// �˳�
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
				int saveChoose = JOptionPane.showConfirmDialog(this, "�ļ���δ���棬�Ƿ񱣴棿", "����",
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
					statusBar.setText("�˳�ʧ��");
					return;
				}
			}
		}
		// �˳�����

		// ����
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
		// ��������

		// ����
		else if (e.getSource() == editMenu_Cut || e.getSource() == popupMenu_Cut) {
			editArea.requestFocus();
			String text = editArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ�����ܵĿ�����
		}
		// ���н���

		// ����
		else if (e.getSource() == editMenu_Copy || e.getSource() == popupMenu_Copy) {
			editArea.requestFocus();
			String text = editArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ�����ܵĿ�����
		}
		// ���ƽ���

		// ճ��
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
		// ճ������

		// ɾ��
		else if (e.getSource() == editMenu_Delete || e.getSource() == popupMenu_Delete) {
			editArea.requestFocus();
			editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
		}
		// ɾ������

		// ����
		else if (e.getSource() == editMenu_Find) {
			editArea.requestFocus();
			find();
		}
		// ���ҽ���

		// ������һ��
		else if (e.getSource() == editMenu_FindNext) {
			editArea.requestFocus();
			find();
		}
		// ������һ������

		// �滻
		else if (e.getSource() == editMenu_Replace) {
			editArea.requestFocus();
			replace();
		}
		// �滻����

		// ת��
		else if (e.getSource() == editMenu_Goto) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
		}
		// ת������

		// ȫѡ
		else if (e.getSource() == editMenu_SelectAll || e.getSource() == popupMenu_SelectAll) {
			editArea.selectAll();
		}
		// ȫѡ����

		// ʱ������
		else if (e.getSource() == editMenu_TimeDate) {
			editArea.requestFocus();
			Calendar rightNow = Calendar.getInstance();
			Date date = rightNow.getTime();
			editArea.insert(date.toString(), editArea.getCaretPosition());
		}
		// ʱ�����ڽ���

		// ��������
		else if (e.getSource() == formatMenu_Font) {
			editArea.requestFocus();
			font();
		}
		// �������ý���

		// �Զ�����(����ǰ������)
		else if (e.getSource() == formatMenu_LineWrap) {
			if (formatMenu_LineWrap.getState())
				editArea.setLineWrap(true);
			else
				editArea.setLineWrap(false);
		}
		// �Զ����н���

		// ����״̬���ɼ���
		else if (e.getSource() == viewMenu_Status) {
			if (viewMenu_Status.getState())
				statusBar.setVisible(true);
			else
				statusBar.setVisible(false);
		}
		// ����״̬���ɼ��Խ���

		// �鿴����
		else if (e.getSource() == helpMenu_ViewHelp) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�´������ղ��ڣ�Ϊ����������", "�鿴����", JOptionPane.INFORMATION_MESSAGE);
		}
		// �鿴��������

		// ���ڼ��±�
		else if (e.getSource() == helpMenu_AboutNotepad) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�˼��±�ֻ��ѧϰ��;", "���ڼ��±�", JOptionPane.INFORMATION_MESSAGE);
		}
		// ���ڼ��±�����

		// �ļ����¼�
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
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
			System.out.println("�ɹ�����");
		}
	}
	// ����actionPerformed()����

	// ʵ��DocumentListener�ӿ��еķ�����һ��������(�볷�������й�)
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

	// ʵ�ֽӿ�UndoableEditListener����UndoHandler(�볷�������й�)
	class UndoHandler implements UndoableEditListener {
		@Override
		public void undoableEditHappened(UndoableEditEvent uee) {
			// TODO Auto-generated method stub
			undomanager.addEdit(uee.getEdit());
		}
	}

	// ����ʵ�ַ���
	public void New() {
		j++;
		// new LockedText();
		editArea.replaceRange("", 0, editArea.getText().length());
		statusBar.setText("�½��ļ�" + "(" + (j) + ")");
		this.setTitle("�ޱ��� - ���±�");
		isNewFile = true;
		undomanager.discardAllEdits();// �������е�"����"����
		editMenu_Undo.setEnabled(false);
		oldValue = editArea.getText();

		i++;
		New(container2[i]);
		container1[i].setVisible(true);
		container1[i].addActionListener(this);
		container1[i].setText("�½��ļ�" + "(" + (j) + ")");
	}

	public void New(JTextArea textArea) {
		// new LockedText();
		// textArea.replaceRange("", 0, editArea.getText().length());
		// statusBar.setText("�½��ļ�"+ "(" + j + ")");
		// this.setTitle("�ޱ��� - ���±�");
		// isNewFile = true;
		// undomanager.discardAllEdits();// �������е�"����"����
		// editMenu_Undo.setEnabled(false);
		// oldValue = editArea.getText();

		System.out.println("�ɹ���������");

	}

	public void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("���ļ�");
		int result = fileChooser.showOpenDialog(this);
		File fileName = fileChooser.getSelectedFile();
		if (result == JFileChooser.CANCEL_OPTION) {
			statusBar.setText("��û��ѡ���κ��ļ�����ʧ��");
			return;
		}
		if (fileName == null || fileName.getName().equals("")) {
			JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "����", JOptionPane.ERROR_MESSAGE);
			statusBar.setText("���Ϸ����ļ�������ʧ�� ");
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
				this.setTitle(fileName.getName() + " - ���±�");
				statusBar.setText("�򿪳ɹ�" + "��ǰ���ļ���" + fileName.getAbsoluteFile());

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
			// this.setTitle(fileName.getName() + " - ���±�");
			// statusBar.setText("�򿪳ɹ�" + "��ǰ���ļ���" + fileName.getAbsoluteFile());

			System.out.println("�ɹ���������");
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
			statusBar.setText("����ɹ�" + " ��ǰ�ļ�·����" + currentFile.getAbsoluteFile());
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
		fileChooser.setDialogTitle("���Ϊ");
		int result = fileChooser.showSaveDialog(this);
		File saveAsFileName = fileChooser.getSelectedFile();
		if (result == JFileChooser.CANCEL_OPTION) {
			statusBar.setText("��û��ѡ���κ��ļ������Ϊʧ��");
			return;
		}
		if (saveAsFileName == null || saveAsFileName.getName().equals("")) {
			JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "����", JOptionPane.ERROR_MESSAGE);
			statusBar.setText("���Ϸ����ļ��������Ϊʧ��");
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
				this.setTitle(saveAsFileName.getName() + " - ���±�");
				statusBar.setText("���Ϊ�ɹ�" + " ��ǰ�ļ�·����" + saveAsFileName.getAbsoluteFile());
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
		JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
	}

	public void find() {
		// ����"����"�Ի���Ľ���
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();
		jp4.setBorder(BorderFactory.createTitledBorder("����"));
		JLabel jl = new JLabel("��������(N)��");
		JTextField jt = new JTextField(20);
		JButton jbt1 = new JButton("������һ��(F)��");
		JButton jbt2 = new JButton("ȡ��");
		JCheckBox jcb = new JCheckBox("���ִ�Сд(C)");
		JRadioButton jrb1 = new JRadioButton("����(U)");
		JRadioButton jrb2 = new JRadioButton("����(D)");
		ButtonGroup btg = new ButtonGroup();
		JDialog findDialog = new JDialog(this, "����", false);
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

		// �¼�����

		// ȡ����ť�¼�����
		jbt2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findDialog.dispose();
			}
		});

		// "������һ��"��ť����
		jbt1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // "���ִ�Сд(C)"��JCheckBox�Ƿ�ѡ��
				int k = 0, m = 0;
				final String str1, str2, str3, str4, strA, strB;
				str1 = editArea.getText();
				str2 = jt.getText();
				str3 = str1.toUpperCase();
				str4 = str2.toUpperCase();
				if (jcb.isSelected())// ���ִ�Сд
				{
					strA = str1;
					strB = str2;
				} else// �����ִ�Сд,��ʱ����ѡ����ȫ�����ɴ�д(��Сд)���Ա��ڲ���
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
						JOptionPane.showMessageDialog(null, "�Ҳ��������ҵ����ݣ�", "����", JOptionPane.INFORMATION_MESSAGE);
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
						JOptionPane.showMessageDialog(null, "�Ҳ��������ҵ����ݣ�", "����", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});

	}

	public void replace() {
		editArea.requestFocus();
		JOptionPane.showMessageDialog(this, "�˹�����δʵ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
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
			int saveChoose = JOptionPane.showConfirmDialog(this, "�ļ���δ���棬�Ƿ񱣴棿", "����",
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
				statusBar.setText("�˳�ʧ��");
				return;
			}
		}
	}

	// ������
	public static void main(String args[]) {
		new LockedText1();
	}
}
