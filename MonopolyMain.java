

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.*;



public class MonopolyMain extends JFrame{

    private JPanel contentIncluder;
    static JTextArea infoConsole;
    JPanel playerAssetsPanel;
    JPanel Special_Square;
    CardLayout c1 = new CardLayout();
    ArrayList<Player> players = new ArrayList<Player>();
    static int turnCounter = 0;
    static int JailTurn1 = 0;
    static int JailTurn2 = 0;
    JButton btnNextTurn;
    JButton btnRollDice;
    JButton btnPayRent;
    JButton btnBuy;
    JTextArea panelPlayer1TextArea;
    JTextArea panelPlayer2TextArea;
    Board gameBoard;
    Player player1;
    Player player2;
    Boolean doubleDiceForPlayer1 = false;
    Boolean doubleDiceForPlayer2 = false;
    static int nowPlaying = 0;
    JLayeredPane layeredPane;

    public MonopolyMain() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(50, 10, 150, 10);
        setSize(1500,1220);
        contentIncluder = new JPanel();
        contentIncluder.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentIncluder);
        contentIncluder.setLayout(null);

        layeredPane = new JLayeredPane();
        layeredPane.setBorder(new LineBorder(new Color(0, 0, 0)));
        layeredPane.setBounds(10, 10, 1352, 922);
        contentIncluder.add(layeredPane);

        gameBoard = new Board(10,10,60,60);
        gameBoard.setBackground(new Color(255, 239, 173));
        layeredPane.add(gameBoard, new Integer(0));

        player1 = new Player(1, Color.DARK_GRAY);
        players.add(player1);
        layeredPane.add(player1, new Integer(1));

        player2 = new Player(2, Color.CYAN);
        players.add(player2);
        layeredPane.add(player2, new Integer(1));

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        rightPanel.setBounds(1432, 10, 419, 892);
        contentIncluder.add(rightPanel);
        rightPanel.setLayout(null);

        btnBuy = new JButton("Buy");
        btnBuy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //turnCounter--; // decrease because we increased at the end of the rolldice
                Player currentPlayer = players.get(nowPlaying);
                //chua mua
                if (!gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getBuyable()) {
                    infoConsole.setText("You bought "+gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName());
                    currentPlayer.buyTitleDeed(currentPlayer.getCurrentSquareNumber());
                    int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getPrice();
                    currentPlayer.withdrawFromWallet(withdrawAmount);
                    gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).setBuy();}
                else
                    //da mua
                    if (Player.ledger.get(currentPlayer.getCurrentSquareNumber()) == currentPlayer.getPlayerNumber()) {
                        infoConsole.setText("You upgrade "+gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName());
                        int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getPrice();
                        currentPlayer.withdrawFromWallet(withdrawAmount);
                        gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).incUPGRADE();
                    }
                btnBuy.setEnabled(false);
                updatePanelPlayer1TextArea();
                updatePanelPlayer2TextArea();
                //turnCounter++;
            }
        });
        btnBuy.setBounds(81, 478, 117, 29);
        rightPanel.add(btnBuy);
        btnBuy.setEnabled(false);

        btnPayRent = new JButton("Pay Rent");
        btnPayRent.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                // turnCounter--;
                Player currentPlayer = players.get(nowPlaying);
                Player ownerOfTheSquare = players.get((Player.ledger.get(currentPlayer.getCurrentSquareNumber()))==1?0:1);
                infoConsole.setText("You paid to the player "+ownerOfTheSquare.getPlayerNumber());
                int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getRentPrice();
                System.out.println(withdrawAmount);
                currentPlayer.withdrawFromWallet(withdrawAmount);
                ownerOfTheSquare.depositToWallet(withdrawAmount);

                btnNextTurn.setEnabled(true);
                btnPayRent.setEnabled(false);
                //currentPlayer.withdrawFromWallet(withdrawAmount);
                updatePanelPlayer1TextArea();
                updatePanelPlayer2TextArea();
                //turnCounter++;
                //gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()).setRentPaid(true);
            }

        });
        btnPayRent.setBounds(210, 478, 117, 29);
        rightPanel.add(btnPayRent);
        btnPayRent.setEnabled(false);

        final Dice dice1 = new Dice(220, 726, 60, 60);
        layeredPane.add(dice1, new Integer(1));

        btnRollDice = new JButton("Roll Dice");
        btnRollDice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(nowPlaying == 0) {
                    // player1's turn
                    if (JailTurn1 == 0) {
                        int dice1OldValue = dice1.getFaceValue();
                        dice1.rollDice();
                        int dicesTotal = dice1.getFaceValue();
                        player1.move(dicesTotal);
                        if(Player.ledger.containsKey(player1.getCurrentSquareNumber()) // if bought by someone
                                && Player.ledger.get(player1.getCurrentSquareNumber()) != player1.getPlayerNumber() // not by itself
                        ) {
                            btnBuy.setEnabled(false);
                            btnRollDice.setEnabled(false);
                            btnNextTurn.setEnabled(false);
                            btnPayRent.setEnabled(true);
                        }
                        if (Player.ledger.containsKey(player1.getCurrentSquareNumber()) // if bought by someone
                                && Player.ledger.get(player1.getCurrentSquareNumber()) == player1.getPlayerNumber()) { // and by itself
                            if (gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()).getUPGRADE()<=3) btnBuy.setEnabled(true);//upgarde duoc khong
                            else btnBuy.setEnabled(false);
                            btnPayRent.setEnabled(false);
                            btnNextTurn.setEnabled(true);
                        }
                        if(gameBoard.getUnbuyableSquares().contains(gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()))) {
                            if (gameBoard.getChange().contains(gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()))) {
                                player1.Change();
                                player1.MinusPlusPoint();
                            }
                            if (gameBoard.getVao_Tu().contains(gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()))) {
                                JOptionPane.showMessageDialog(null, "Go to jail for 3 turns");
                                player1.move(30 - player1.getCurrentSquareNumber());
                                JailTurn1 = 3;
                            }
                            btnBuy.setEnabled(false);
                            btnNextTurn.setEnabled(true);
                        } else if (!Player.ledger.containsKey(player1.getCurrentSquareNumber())) { // if not bought by someone
                            btnBuy.setEnabled(true);
                            btnNextTurn.setEnabled(true);
                            btnPayRent.setEnabled(false);
                        }
                    }
                    else{
                        btnBuy.setEnabled(false);
                        btnRollDice.setEnabled(false);
                        btnPayRent.setEnabled(false);
                        btnNextTurn.setEnabled(true);
                        JailTurn1--;
                    }


                } else {
                    // player2's turn
                    if (JailTurn2 == 0) {
                        int dice1OldValue = dice1.getFaceValue();
                        dice1.rollDice();
                        int dicesTotal = dice1.getFaceValue() ;
                        player2.move(dicesTotal);
                        if(Player.ledger.containsKey(player2.getCurrentSquareNumber()) // if bought by someone
                                && Player.ledger.get(player2.getCurrentSquareNumber()) != player2.getPlayerNumber() // not by itself
                        ) {
                            btnBuy.setEnabled(false);
                            btnRollDice.setEnabled(false);
                            btnNextTurn.setEnabled(false);
                            btnPayRent.setEnabled(true);
                        }
                        if(Player.ledger.containsKey(player2.getCurrentSquareNumber()) // if bought by someone
                                && Player.ledger.get(player2.getCurrentSquareNumber()) == player2.getPlayerNumber()) { // and by itself
                            if (gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()).getUPGRADE()<3) btnBuy.setEnabled(true);//upgarde duoc khong
                            else btnBuy.setEnabled(false);
                            btnPayRent.setEnabled(false);
                            btnNextTurn.setEnabled(true);

                        }
                        if(gameBoard.getUnbuyableSquares().contains(gameBoard.getAllSquares().get(player2.getCurrentSquareNumber()))) {
                            if (gameBoard.getChange().contains(gameBoard.getAllSquares().get(player2.getCurrentSquareNumber()))) {
                                player2.Change();
                                player2.MinusPlusPoint();
                            }
                            if (gameBoard.getVao_Tu().contains(gameBoard.getAllSquares().get(player2.getCurrentSquareNumber()))) {
                                JOptionPane.showMessageDialog(null, "Go to jail for 3 turns");
                                player2.move(30 - player2.getCurrentSquareNumber());
                                JailTurn2 = 3;
                            }
                            btnBuy.setEnabled(false);
                            btnNextTurn.setEnabled(true);
                        } else if (!Player.ledger.containsKey(player2.getCurrentSquareNumber())) { // if not bought by someone
                            btnBuy.setEnabled(true);
                            btnNextTurn.setEnabled(true);
                            btnPayRent.setEnabled(false);
                        }
                    }
                    else{
                        btnBuy.setEnabled(false);
                        btnRollDice.setEnabled(false);
                        btnPayRent.setEnabled(false);
                        btnNextTurn.setEnabled(true);
                        JailTurn2--;
                    }

                }

                btnRollDice.setEnabled(false);
                if(doubleDiceForPlayer1 || doubleDiceForPlayer2) {
                    infoConsole.setText("Click Next Turn to allow player "+ (nowPlaying==0 ? 1 : 2) +" to Roll Dice!");
                } else {
                    infoConsole.setText("Click Next Turn to allow player "+ (nowPlaying==0 ? 2 : 1) +" to Roll Dice!");
                }


                // we have to add below 2 lines to avoid some GUI breakdowns.
                layeredPane.remove(gameBoard);
                layeredPane.add(gameBoard, new Integer(0));

                updatePanelPlayer1TextArea();
                updatePanelPlayer2TextArea();

            }
        });
        btnRollDice.setBounds(81, 413, 246, 53);
        rightPanel.add(btnRollDice);

        btnNextTurn = new JButton("Next Turn");
        btnNextTurn.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                btnRollDice.setEnabled(true);
                btnBuy.setEnabled(false);
                btnPayRent.setEnabled(false);
                btnNextTurn.setEnabled(false);

                if(nowPlaying == 0 && doubleDiceForPlayer1) {
                    nowPlaying = 0;
                    doubleDiceForPlayer1 = false;
                } else if(nowPlaying == 1 && doubleDiceForPlayer2) {
                    nowPlaying = 1;
                    doubleDiceForPlayer2 = false;
                } else if(!doubleDiceForPlayer1 && !doubleDiceForPlayer2) {
                    nowPlaying = (nowPlaying + 1) % 2;
                }


                c1.show(playerAssetsPanel, ""+(nowPlaying==0 ? 1 : 2)); // maps 0 to 1 and 1 to 2
                updatePanelPlayer1TextArea();
                updatePanelPlayer2TextArea();
                infoConsole.setText("It's now player "+(nowPlaying==0 ? 1 : 2)+"'s turn!");
            }



        });

        btnNextTurn.setBounds(81, 519, 246, 53); //81, 478, 117, 29
        rightPanel.add(btnNextTurn);
        btnNextTurn.setEnabled(false);

        JPanel test = new JPanel();
        test.setBounds(81, 312, 246, 68);
        rightPanel.add(test);
        test.setLayout(null);

        playerAssetsPanel = new JPanel();
        playerAssetsPanel.setBounds(81, 28, 246, 189);
        rightPanel.add(playerAssetsPanel);
        playerAssetsPanel.setLayout(c1);

        JPanel panelPlayer1 = new JPanel();
        panelPlayer1.setBackground(Color.RED);
        playerAssetsPanel.add(panelPlayer1, "1");
        panelPlayer1.setLayout(null);

        JLabel panelPlayer1Title = new JLabel("Player 1 All Wealth");
        panelPlayer1Title.setForeground(Color.WHITE);
        panelPlayer1Title.setHorizontalAlignment(SwingConstants.CENTER);
        panelPlayer1Title.setBounds(0, 6, 240, 16);
        panelPlayer1.add(panelPlayer1Title);

        panelPlayer1TextArea = new JTextArea();
        panelPlayer1TextArea.setBounds(10, 34, 230, 149);
        panelPlayer1.add(panelPlayer1TextArea);

        JPanel panelPlayer2 = new JPanel();
        panelPlayer2.setBackground(Color.BLUE);
        playerAssetsPanel.add(panelPlayer2, "2");
        panelPlayer2.setLayout(null);
        c1.show(playerAssetsPanel, ""+nowPlaying);

        JLabel panelPlayer2Title = new JLabel("Player 2 All Wealth");
        panelPlayer2Title.setForeground(Color.WHITE);
        panelPlayer2Title.setHorizontalAlignment(SwingConstants.CENTER);
        panelPlayer2Title.setBounds(0, 6, 240, 16);
        panelPlayer2.add(panelPlayer2Title);

        panelPlayer2TextArea = new JTextArea();
        panelPlayer2TextArea.setBounds(10, 34, 230, 149);
        panelPlayer2.add(panelPlayer2TextArea);

        updatePanelPlayer1TextArea();
        updatePanelPlayer2TextArea();

        //panel_CoHoi.setVisible(false);

        infoConsole = new JTextArea();
        infoConsole.setColumns(20);
        infoConsole.setRows(5);
        infoConsole.setBounds(6, 6, 234, 56);
        test.add(infoConsole);
        infoConsole.setLineWrap(true);
        infoConsole.setText("PLayer 1 starts the game by clicking Roll Dice!");

    }


    public void updatePanelPlayer2TextArea() {
        // TODO Auto-generated method stub
        String result = "";
        result += "Current Balance: "+player2.getWallet()+"\n";

        result += "Title Deeds: \n";
        for(int i = 0; i < player2.getTitleDeeds().size(); i++) {
            result += " - "+gameBoard.getAllSquares().get(player2.getTitleDeeds().get(i)).getName()+"\n";
        }

        panelPlayer2TextArea.setText(result);
    }

    public void updatePanelPlayer1TextArea() {
        // TODO Auto-generated method stub
        String result = "";
        result += "Current Balance: "+player1.getWallet()+"\n";

        result += "Title Deeds: \n";
        for(int i = 0; i < player1.getTitleDeeds().size(); i++) {
            result += " - "+gameBoard.getAllSquares().get(player1.getTitleDeeds().get(i)).getName()+"\n";
        }


        panelPlayer1TextArea.setText(result);
    }

    public static void errorBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.ERROR_MESSAGE);
    }


    public static void main(String[] args) {

        MonopolyMain frame = new MonopolyMain();
        frame.setVisible(true);

    }

}

