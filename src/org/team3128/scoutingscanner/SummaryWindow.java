package org.team3128.scoutingscanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import com.albertoborsetta.formscanner.api.FormTemplate;
import com.albertoborsetta.formscanner.api.commons.Constants.CornerType;
import com.albertoborsetta.formscanner.api.exceptions.FormScannerException;

public class SummaryWindow {

	private JFrame frame;
	private JFileChooser fc = new JFileChooser();
	private JPanel addSheetPanel;
	private JLabel sheetPreviewLabel;
	private JButton chooseImageButton;
	private JLabel teamNumberLabel;
	private JTextField teamNumberTextField;
	private JTextField matchNumberTextField;
	private JLabel matchNumberLabel;
	private JButton btnProcessSheet;
	private JScrollPane scrollPane;
	private JPanel matchList;
	
	static File templateFile;
	static FormTemplate templateForm;
	
	static BufferedImage matchSheetImage;
	
	static HashMap<Integer, ArrayList<SheetData>> sheetData = new HashMap<Integer, ArrayList<SheetData>>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				BufferedWriter bw = null;
				FileWriter fw = null;
				try {
					File templateTempFile = File.createTempFile("template-", ".xtmpl");					
					templateTempFile.deleteOnExit();

					fw = new FileWriter(templateTempFile);
					bw = new BufferedWriter(fw);
					bw.write(getTemplate());
					
					bw.close();
					
					templateForm = new FormTemplate(templateTempFile);
					
					SummaryWindow window = new SummaryWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SummaryWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTeamScouting = new JLabel("Team 3128: Scouting Sheet Scanner");
		lblTeamScouting.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeamScouting.setBounds(6, 6, 688, 24);
		lblTeamScouting.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
		frame.getContentPane().add(lblTeamScouting);
		
		addSheetPanel = new JPanel();
		addSheetPanel.setBounds(0, 315, 700, 163);
		addSheetPanel.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(addSheetPanel);
		addSheetPanel.setLayout(null);
		
		sheetPreviewLabel = new JLabel("Sheet Preview");
		sheetPreviewLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
		sheetPreviewLabel.setBounds(0, 0, 150, 125);
		sheetPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		addSheetPanel.add(sheetPreviewLabel);
		
		chooseImageButton = new JButton("Choose Image");
		chooseImageButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
				fc.addChoosableFileFilter(imageFilter);
				
				int chosen = fc.showOpenDialog(chooseImageButton);
				
				if (chosen == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            BufferedImage sheetImage;
					try 
					{
						sheetImage = ImageIO.read(file);
						matchSheetImage = sheetImage;
						BufferedImage previewImage = sheetImage.getSubimage((int) Math.round(sheetImage.getWidth() * .04), (int) Math.round(sheetImage.getHeight() * .04), (int) Math.round(sheetImage.getWidth() * .275), (int) Math.round(sheetImage.getHeight() * .17));
			            ImageIcon previewImageIcon = new ImageIcon(previewImage.getScaledInstance(sheetPreviewLabel.getWidth(), sheetPreviewLabel.getHeight(), Image.SCALE_SMOOTH));
			            sheetPreviewLabel.setIcon(previewImageIcon);
			            sheetPreviewLabel.setText("");
					} 
					catch (IOException e1) 
					{
						JOptionPane.showMessageDialog(null, "That's not an image...");
					}
					catch (NullPointerException e2)
					{
						JOptionPane.showMessageDialog(null, "That's not an image...");
					}
				}
			}
		});
		chooseImageButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		chooseImageButton.setBounds(162, 87, 532, 29);
		addSheetPanel.add(chooseImageButton);
		
		matchNumberLabel = new JLabel("Match Number:");
		matchNumberLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		matchNumberLabel.setBounds(162, 59, 98, 16);
		addSheetPanel.add(matchNumberLabel);
		
		matchNumberTextField = new JTextField();
		matchNumberTextField.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		matchNumberTextField.setBounds(262, 54, 130, 26);
		matchNumberTextField.setColumns(10);
		addSheetPanel.add(matchNumberTextField);
		
		teamNumberLabel = new JLabel("Team Number:");
		teamNumberLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		teamNumberLabel.setBounds(162, 30, 98, 16);
		addSheetPanel.add(teamNumberLabel);
		
		teamNumberTextField = new JTextField();
		teamNumberTextField.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		teamNumberTextField.setBounds(262, 24, 323, 29);
		teamNumberTextField.setColumns(10);
		addSheetPanel.add(teamNumberTextField);
		
		
		btnProcessSheet = new JButton("Process Sheet");
		btnProcessSheet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isInteger(matchNumberTextField.getText()) && isInteger(teamNumberTextField.getText()))
				{
					if (matchSheetImage != null)
					{
						int matchNumber = Integer.parseInt(matchNumberTextField.getText());
						int teamNumber = Integer.parseInt(teamNumberTextField.getText());
						
						FormTemplate filledForm = new FormTemplate("Filled Sheet", templateForm);
						try {
							HashMap<String, Integer> crop = new HashMap<String, Integer>();
							crop.put("TOP", 0);
							crop.put("LEFT", 0);
							crop.put("RIGHT", 0);
							crop.put("BOTTOM", 0);
							filledForm.findCorners(matchSheetImage, 150, 40, CornerType.ROUND, crop);
							filledForm.findPoints(matchSheetImage, 150, 40, 20);
							
							if (sheetData.containsKey(matchNumber))
							{
								ArrayList<SheetData> oldSheetData = sheetData.get(matchNumber);
								oldSheetData.add(new SheetData(teamNumber, filledForm.getXml()));
								sheetData.put(matchNumber, oldSheetData);
							}
							else
							{
								sheetData.put(matchNumber, new ArrayList<SheetData>(Arrays.asList(new SheetData(teamNumber, filledForm.getXml()))));
							}
							
							refreshMatchList();
							
							matchNumberTextField.setText("");
							teamNumberTextField.setText("");
							matchSheetImage = null;
							sheetPreviewLabel.setIcon(null);
							sheetPreviewLabel.setText("Sheet Preview");
							
						} catch (FormScannerException e1) {
							JOptionPane.showMessageDialog(null, "Something went wrong... That sheet may not be usable.");
						} catch (ParserConfigurationException e2) {
							JOptionPane.showMessageDialog(null, "Something went wrong... That sheet may not be usable.");
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "No sheet image uploaded.");
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "Match Number or Team Number fields not properly filled");
				}
   			}
		});
		btnProcessSheet.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		btnProcessSheet.setBounds(10, 128, 684, 29);
		addSheetPanel.add(btnProcessSheet);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 35, 700, 280);
		scrollPane.setBorder(null);
		frame.getContentPane().add(scrollPane);
		
		matchList = new JPanel();
		matchList.setBorder(null);
		matchList.setBackground(Color.WHITE);
		
		scrollPane.setViewportView(matchList);
		matchList.setLayout(new BoxLayout(matchList, BoxLayout.Y_AXIS));
		
		JButton btnClearData = new JButton("Clear Data");
		btnClearData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int delete = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all of the match data? This is an irreversible action.", "Delete All Scouting Data?", JOptionPane.YES_NO_OPTION);
		        if (delete == JOptionPane.YES_OPTION) {
		          sheetData.clear();
		          refreshMatchList();
		        }
			}
		});
		btnClearData.setBounds(16, 4, 117, 29);
		frame.getContentPane().add(btnClearData);
	}
	
	private void refreshMatchList()
	{
		matchList.removeAll();
		ArrayList<Integer> teamNumbers = new ArrayList<Integer>();
		
		Integer[] matchNumbers = sheetData.keySet().toArray(new Integer[sheetData.keySet().size()]);
		Arrays.sort(matchNumbers);

		for (int matchNumber : matchNumbers)
		{
			for (SheetData element : sheetData.get(matchNumber))
			{
				teamNumbers.add(element.teamNumber);
			}
			matchList.add(matchPanel(matchNumber, teamNumbers), 0);
			teamNumbers.clear();
		}
		
		matchList.revalidate();
		matchList.repaint();
	}
	
	private JPanel matchPanel(int matchNumber, ArrayList<Integer> arrayList) {
		JPanel panel = new JPanel();
		
		panel.setLayout(null);
		
		JLabel lblMatch = new JLabel("Match " + matchNumber);
		lblMatch.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
		lblMatch.setHorizontalAlignment(SwingConstants.CENTER);
		lblMatch.setBounds(5, 5, 120, 69);
		panel.add(lblMatch);
		
		String teamsString = "";
		for (int team : arrayList) {
			teamsString += team + ", ";
		}
		
		JLabel lblTeams = new JLabel("Teams: " + teamsString);
		lblTeams.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
		lblTeams.setBounds(135, 6, 350, 68);
		panel.add(lblTeams);
		
		JButton btnCopyToClipboard = new JButton("Copy to Clipboard");
		btnCopyToClipboard.setBounds(499, 5, 195, 69);
		btnCopyToClipboard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String copyString = "";
				for (SheetData sheet : sheetData.get(matchNumber))
				{
					copyString += matchNumber + "	";
					copyString += sheet.teamNumber + "	";
					copyString += sheet.getData(Constants.AUTO_HIGHGOAL) + "	";
					copyString += sheet.getData(Constants.AUTO_LOWGOAL) + "	";
					copyString += sheet.getData(Constants.REACHED) + "	";
					copyString += sheet.getData(Constants.AUTO_GEAR) + "	";
					copyString += sheet.getData(Constants.HIGHGOAL) + "	";
					copyString += sheet.getData(Constants.LOWGOAL) + "	";
					copyString += sheet.getData(Constants.GEARS) + "	";
					copyString += sheet.getData(Constants.SCALED) + "	";
					copyString += sheet.getData(Constants.FOULS) + "	";
					copyString += sheet.getData(Constants.TECHFOULS) + "	";
					copyString += sheet.getData(Constants.CARDS) + "\n";
				}
				StringSelection selection = new StringSelection(copyString);
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			    
			    JOptionPane pane = new JOptionPane("Copied Match " + matchNumber + " data to clipboard.");
			    final JDialog dialog = pane.createDialog("Copied.");
			    Timer timer = new Timer(750, new ActionListener() {
			        public void actionPerformed(ActionEvent e) {
			            dialog.setVisible(false);
			        }
			    });
			    timer.setRepeats(false);
			    timer.start();
			    dialog.setVisible(true);
			}
		});
		panel.add(btnCopyToClipboard);
        
		int panelWidth = 700;
		int panelHeight = 80;
        panel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        panel.setMinimumSize(new Dimension(panelWidth, panelHeight));
        panel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        
        panel.setBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE));
        panel.setBackground(new Color(220, 220, 220));
        
		return panel;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	private static String getTemplate()
	{
		String template = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<template density=\"40\" threshold=\"127\" version=\"2.1\">\n    <crop bottom=\"0\" left=\"0\" right=\"0\" top=\"0\"/>\n    <rotation angle=\"0.0\"/>\n    <corners type=\"ROUND\">\n        <corner position=\"TOP_LEFT\">\n            <point x=\"70.0\" y=\"70.0\"/>\n        </corner>\n        <corner position=\"TOP_RIGHT\">\n            <point x=\"1203.0\" y=\"70.0\"/>\n        </corner>\n        <corner position=\"BOTTOM_RIGHT\">\n            <point x=\"1203.0\" y=\"1578.0\"/>\n        </corner>\n        <corner position=\"BOTTOM_LEFT\">\n            <point x=\"70.0\" y=\"1578.0\"/>\n        </corner>\n    </corners>\n    <fields groups=\"true\" shape=\"CIRCLE\" size=\"10\">\n        <group name=\"auto\">\n            <question multiple=\"true\" question=\"auto-highgoal\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"0\">\n                        <point x=\"501.0\" y=\"657.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"501.0\" y=\"717.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"501.0\" y=\"777.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"501.0\" y=\"837.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"501.0\" y=\"897.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"575.0\" y=\"657.0\"/>\n                    </value>\n                    <value response=\"6\">\n                        <point x=\"575.0\" y=\"717.0\"/>\n                    </value>\n                    <value response=\"7\">\n                        <point x=\"575.0\" y=\"777.0\"/>\n                    </value>\n                    <value response=\"8\">\n                        <point x=\"575.0\" y=\"837.0\"/>\n                    </value>\n                    <value response=\"9\">\n                        <point x=\"575.0\" y=\"897.0\"/>\n                    </value>\n                    <value response=\"70\">\n                        <point x=\"575.0\" y=\"957.0\"/>\n                    </value>\n                    <value response=\"60\">\n                        <point x=\"501.0\" y=\"957.0\"/>\n                    </value>\n                    <value response=\"50\">\n                        <point x=\"427.0\" y=\"957.0\"/>\n                    </value>\n                    <value response=\"40\">\n                        <point x=\"427.0\" y=\"897.0\"/>\n                    </value>\n                    <value response=\"30\">\n                        <point x=\"427.0\" y=\"837.0\"/>\n                    </value>\n                    <value response=\"20\">\n                        <point x=\"427.0\" y=\"777.0\"/>\n                    </value>\n                    <value response=\"10\">\n                        <point x=\"427.0\" y=\"717.0\"/>\n                    </value>\n                    <value response=\"0\">\n                        <point x=\"427.0\" y=\"657.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"auto-reach\" rejectMultiple=\"false\" type=\"QUESTIONS_BY_ROWS\">\n                <values>\n                    <value response=\"Y\">\n                        <point x=\"557.0\" y=\"209.0\"/>\n                    </value>\n                    <value response=\"N\">\n                        <point x=\"444.0\" y=\"209.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"auto-lowgoal\" rejectMultiple=\"false\" type=\"QUESTIONS_BY_ROWS\">\n                <values>\n                    <value response=\"Y\">\n                        <point x=\"557.0\" y=\"312.0\"/>\n                    </value>\n                    <value response=\"N\">\n                        <point x=\"444.0\" y=\"312.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"auto-gear\" rejectMultiple=\"false\" type=\"QUESTIONS_BY_ROWS\">\n                <values>\n                    <value response=\"Y\">\n                        <point x=\"557.0\" y=\"415.0\"/>\n                    </value>\n                    <value response=\"N\">\n                        <point x=\"444.0\" y=\"415.0\"/>\n                    </value>\n                </values>\n            </question>\n        </group>\n        <group name=\"endgame\">\n            <question multiple=\"false\" question=\"endgame-scaled\" rejectMultiple=\"false\" type=\"QUESTIONS_BY_ROWS\">\n                <values>\n                    <value response=\"Y\">\n                        <point x=\"1107.0\" y=\"957.0\"/>\n                    </value>\n                    <value response=\"N\">\n                        <point x=\"993.0\" y=\"957.0\"/>\n                    </value>\n                </values>\n            </question>\n        </group>\n        <group name=\"violations\">\n            <question multiple=\"false\" question=\"violations-fouls\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"0\">\n                        <point x=\"150.0\" y=\"485.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"150.0\" y=\"546.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"225.0\" y=\"485.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"225.0\" y=\"546.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"300.0\" y=\"485.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"300.0\" y=\"546.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"violations-cards\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"0\">\n                        <point x=\"151.0\" y=\"900.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"151.0\" y=\"960.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"225.0\" y=\"900.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"225.0\" y=\"960.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"299.0\" y=\"900.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"299.0\" y=\"960.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"violations-techfouls\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"0\">\n                        <point x=\"151.0\" y=\"692.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"151.0\" y=\"752.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"225.0\" y=\"692.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"225.0\" y=\"752.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"300.0\" y=\"692.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"300.0\" y=\"752.0\"/>\n                    </value>\n                </values>\n            </question>\n        </group>\n        <group name=\"teleop\">\n            <question multiple=\"true\" question=\"teleop-highgoal\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"90\">\n                        <point x=\"775.0\" y=\"958.0\"/>\n                    </value>\n                    <value response=\"70\">\n                        <point x=\"775.0\" y=\"837.0\"/>\n                    </value>\n                    <value response=\"50\">\n                        <point x=\"775.0\" y=\"717.0\"/>\n                    </value>\n                    <value response=\"30\">\n                        <point x=\"775.0\" y=\"597.0\"/>\n                    </value>\n                    <value response=\"10\">\n                        <point x=\"775.0\" y=\"477.0\"/>\n                    </value>\n                    <value response=\"0\">\n                        <point x=\"850.0\" y=\"417.0\"/>\n                    </value>\n                    <value response=\"0\">\n                        <point x=\"775.0\" y=\"417.0\"/>\n                    </value>\n                    <value response=\"0\">\n                        <point x=\"701.0\" y=\"417.0\"/>\n                    </value>\n                    <value response=\"100\">\n                        <point x=\"701.0\" y=\"477.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"850.0\" y=\"477.0\"/>\n                    </value>\n                    <value response=\"200\">\n                        <point x=\"701.0\" y=\"537.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"850.0\" y=\"537.0\"/>\n                    </value>\n                    <value response=\"300\">\n                        <point x=\"701.0\" y=\"597.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"850.0\" y=\"597.0\"/>\n                    </value>\n                    <value response=\"400\">\n                        <point x=\"701.0\" y=\"657.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"850.0\" y=\"657.0\"/>\n                    </value>\n                    <value response=\"500\">\n                        <point x=\"701.0\" y=\"717.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"850.0\" y=\"717.0\"/>\n                    </value>\n                    <value response=\"600\">\n                        <point x=\"701.0\" y=\"777.0\"/>\n                    </value>\n                    <value response=\"6\">\n                        <point x=\"850.0\" y=\"777.0\"/>\n                    </value>\n                    <value response=\"700\">\n                        <point x=\"701.0\" y=\"837.0\"/>\n                    </value>\n                    <value response=\"7\">\n                        <point x=\"850.0\" y=\"837.0\"/>\n                    </value>\n                    <value response=\"800\">\n                        <point x=\"701.0\" y=\"897.0\"/>\n                    </value>\n                    <value response=\"80\">\n                        <point x=\"775.0\" y=\"897.0\"/>\n                    </value>\n                    <value response=\"8\">\n                        <point x=\"850.0\" y=\"897.0\"/>\n                    </value>\n                    <value response=\"900\">\n                        <point x=\"701.0\" y=\"958.0\"/>\n                    </value>\n                    <value response=\"9\">\n                        <point x=\"850.0\" y=\"958.0\"/>\n                    </value>\n                    <value response=\"60\">\n                        <point x=\"775.0\" y=\"777.0\"/>\n                    </value>\n                    <value response=\"40\">\n                        <point x=\"775.0\" y=\"657.0\"/>\n                    </value>\n                    <value response=\"20\">\n                        <point x=\"775.0\" y=\"537.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"teleop-gears\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"11\">\n                        <point x=\"1124.0\" y=\"759.0\"/>\n                    </value>\n                    <value response=\"12\">\n                        <point x=\"1050.0\" y=\"819.0\"/>\n                    </value>\n                    <value response=\"O\">\n                        <point x=\"1124.0\" y=\"819.0\"/>\n                    </value>\n                    <value response=\"0\">\n                        <point x=\"976.0\" y=\"819.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"976.0\" y=\"639.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"976.0\" y=\"699.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"976.0\" y=\"759.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"1050.0\" y=\"580.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"1050.0\" y=\"639.0\"/>\n                    </value>\n                    <value response=\"6\">\n                        <point x=\"1050.0\" y=\"699.0\"/>\n                    </value>\n                    <value response=\"7\">\n                        <point x=\"1050.0\" y=\"759.0\"/>\n                    </value>\n                    <value response=\"8\">\n                        <point x=\"1124.0\" y=\"580.0\"/>\n                    </value>\n                    <value response=\"9\">\n                        <point x=\"1124.0\" y=\"639.0\"/>\n                    </value>\n                    <value response=\"10\">\n                        <point x=\"1124.0\" y=\"699.0\"/>\n                    </value>\n                </values>\n            </question>\n            <question multiple=\"false\" question=\"teleop-lowgoal\" rejectMultiple=\"false\" type=\"RESPONSES_BY_GRID\">\n                <values>\n                    <value response=\"0\">\n                        <point x=\"976.0\" y=\"253.0\"/>\n                    </value>\n                    <value response=\"1\">\n                        <point x=\"976.0\" y=\"313.0\"/>\n                    </value>\n                    <value response=\"2\">\n                        <point x=\"976.0\" y=\"373.0\"/>\n                    </value>\n                    <value response=\"3\">\n                        <point x=\"976.0\" y=\"433.0\"/>\n                    </value>\n                    <value response=\"4\">\n                        <point x=\"1050.0\" y=\"253.0\"/>\n                    </value>\n                    <value response=\"5\">\n                        <point x=\"1050.0\" y=\"313.0\"/>\n                    </value>\n                    <value response=\"6\">\n                        <point x=\"1050.0\" y=\"373.0\"/>\n                    </value>\n                    <value response=\"7\">\n                        <point x=\"1050.0\" y=\"433.0\"/>\n                    </value>\n                    <value response=\"8\">\n                        <point x=\"1124.0\" y=\"253.0\"/>\n                    </value>\n                    <value response=\"9\">\n                        <point x=\"1124.0\" y=\"313.0\"/>\n                    </value>\n                    <value response=\"11\">\n                        <point x=\"1124.0\" y=\"433.0\"/>\n                    </value>\n                    <value response=\"10\">\n                        <point x=\"1124.0\" y=\"373.0\"/>\n                    </value>\n                </values>\n            </question>\n        </group>\n    </fields>\n</template>\n";
		return template;
	}
}
