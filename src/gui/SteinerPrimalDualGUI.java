
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import models.*;

public class SteinerPrimalDualGUI extends JFrame implements MouseListener, ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton edgeButton, terminalButton;
    DisplayPanel graphPanel;
    JButton runAlgoButton;
    JButton connectivityButton;
    JPanel topPanel;
    SPDModel _model;
    boolean toggle = true;
    
    SteinerPrimalDualGUI() {
        _model = new SPDModel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        topPanel = new JPanel();
        topPanel.setSize(new Dimension(500, 50));
        graphPanel = new DisplayPanel(_model);

        graphPanel.setVisible(true);
        this.add(graphPanel);

      //Create the button to add edges
        edgeButton = new JButton("Add Edges");
        
        ActionListener EdgeActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
           
            	Icon optionIcon = UIManager.getIcon("FileView.computerIcon");
                String connect_condition = (String) JOptionPane.showInputDialog(topPanel, "Enter edge and its weight(Format:N1,N2,5)", "Edge and weight", JOptionPane.QUESTION_MESSAGE, optionIcon, null, null);
                if(connect_condition!=null){
                	checkValidStringAndCreateEdges(connect_condition);
                }  
            }
        };
        edgeButton.addActionListener(EdgeActionListener);
        topPanel.add(edgeButton);
        //this.add(edgeButton);
        
      //Create the button to add terminals
        terminalButton = new JButton("Add Connectivity Requirements");
        
        ActionListener TerminalActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
           
            	Icon optionIcon = UIManager.getIcon("FileView.computerIcon");
                String connect_req = (String) JOptionPane.showInputDialog(topPanel, "Enter the connectivity requirement(Format:N1,N2)", "Connectivity Requirement", JOptionPane.QUESTION_MESSAGE, optionIcon, null, null);
                if(connect_req!=null){
                	checkValidStringAndSetTerminals(connect_req);
                }
            }
        };
        terminalButton.addActionListener(TerminalActionListener);
        topPanel.add(terminalButton);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
           
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            new SteinerPDAlgo().runAlgorithm(_model, graphPanel);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SteinerPrimalDualGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();

            }
        };
        
        
        runAlgoButton = new JButton("Run Algo");
        runAlgoButton.setSize(new Dimension(20, 20));
        runAlgoButton.addActionListener(actionListener);
        topPanel.add(runAlgoButton);
        
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(topPanel, BorderLayout.NORTH);
        content.add(graphPanel, BorderLayout.CENTER);

    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    SteinerPrimalDualGUI frame = new SteinerPrimalDualGUI();
                    frame.setVisible(true);

                    frame.addMouseListener(frame);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        _model.addNode(e.getX()-5, e.getY()-65);//offset correction
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
    
    private boolean checkValidStringAndCreateEdges(String connect_condition) {
        boolean valid_string = true;
        connect_condition = connect_condition.replaceAll("[\\s]+", "");
        StringTokenizer edgeSubString = new StringTokenizer(connect_condition, ",");
        if (edgeSubString.countTokens() != 3) {
            valid_string = false;
        }
        String nextSubStr = null;
        String[] edgeStrArr = new String[3];
        int itr = 0;
        while (valid_string && edgeSubString.hasMoreTokens()) {
            nextSubStr = edgeSubString.nextToken();
            if ((itr < 2) && (nextSubStr.contains("N"))) {
                edgeStrArr[itr] = nextSubStr;
            } else if ((itr == 2) && (Integer.valueOf(nextSubStr) != 0)) {
                edgeStrArr[itr] = nextSubStr;
            } else {
                valid_string = false;
            }
            itr++;
        }
        if (valid_string == true) {
            _model.addEdge(edgeStrArr[0], edgeStrArr[1], Integer.valueOf(edgeStrArr[2]));
        }
        return valid_string;
    }
    
    private boolean checkValidStringAndSetTerminals(String connect_req) {
		boolean valid_string = true;
		connect_req = connect_req.replaceAll("[\\s]+", "");
        StringTokenizer nodeStr = new StringTokenizer(connect_req, ",");
        if (nodeStr.countTokens() != 2) {
            valid_string = false;
        }
        String nextSubStr = null;
        String[] nodeStrArr = new String[2];
        int itr = 0;
        while (valid_string && nodeStr.hasMoreTokens()) {
            nextSubStr = nodeStr.nextToken();
            if (nextSubStr.contains("N")) {
            	nodeStrArr[itr] = nextSubStr;
            } else {
                valid_string = false;
            }
            itr++;
        }
        if (valid_string == true) {
            _model.setTerminals(nodeStrArr[0], nodeStrArr[1]);
        }
        return valid_string;
	}

}
