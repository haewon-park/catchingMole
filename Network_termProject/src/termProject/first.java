package termProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import termProject.Main.ButtonAction;

public class first extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					first frame = new first();
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
	public first() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setSize(950,800);
    	setResizable(false);
    	setLocation(360,20);
    	getContentPane().setLayout(null);
    	setVisible(true);
    	
    	JButton loginButton = new JButton("login", new ImageIcon("/loginbutton.jpg"));
    	loginButton.setBorderPainted(false);
    	loginButton.setContentAreaFilled(false);
    	loginButton.setFocusPainted(false);
    	loginButton.setBounds(500, 650, 242, 116);
    	getContentPane().add(loginButton);
    	
    	JButton ruleButton = new JButton("rule", new ImageIcon("/rulebutton.jpg"));
    	ruleButton.setBorderPainted(false);
		ruleButton.setContentAreaFilled(false);
		ruleButton.setFocusPainted(false);
		ruleButton.setBounds(150, 650, 243, 116);
		getContentPane().add(ruleButton);
		
		JLabel mainBG = new JLabel("");
		mainBG.setBounds(0, 0, 950, 800);
		getContentPane().add(mainBG);
		mainBG.setIcon(new ImageIcon("/first.jpg"));
		
	      loginButton.addActionListener(new ButtonAction());
	      ruleButton.addActionListener(new ButtonAction());
    
	}
	 class ButtonAction implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				JButton myButton = (JButton)e.getSource();
				String temp = myButton.getText();
				
				if(temp.equals("login")) 
				{
					new LoginUI();
				}
				else if(temp.equals("rule"))
				{
		            JFrame rule = new JFrame();
		            JLabel ruleimg = new JLabel("");
		            ruleimg.setBounds(0, 0, 916,800);
		            rule.setSize(910,780);
		            ruleimg.setSize(910, 780);
		            
		            rule.setAlwaysOnTop(true);
		            rule.setUndecorated(true);
		            getContentPane().add(ruleimg);
		            ruleimg.setIcon(new ImageIcon("\rule.jpg"));
		            rule.add(ruleimg);
		            rule.setLocation(363, 67);
		            rule.getContentPane().setLayout(null);
		            rule.setVisible(true);
		            
		            
				}
			}
	    	
	    }
	

}
