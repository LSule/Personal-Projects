import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
	private class Card {
		String value;
		String type;
		
		Card(String value, String type) {
			this.value = value;
			this.type = type;
		}
		
		public String toString() {
			return value + "-" + type;
		}
		
		public int getValue() {
			if ("AJQK".contains(value)) { // A J Q K
				if (value == "A") {
					return 11;
				}
				return 10;
			}
			return Integer.parseInt(value); // 2-10
		}
		
		public boolean isAce() {
			return value == "A";
		}
		
		public String getImagePath() {
			return "./cards/" + toString() + ".png";
		}
	}
	
	ArrayList<Card> deck;
	Random random = new Random(); // Shuffle deck
	
	// dealer
	Card hiddenCard;
	ArrayList<Card> dealerHand;
	int dealerSum;
	int dealerAceCount;
	
	// player
	ArrayList<Card> playerHand;
	int playerSum;
	int playerAceCount;
	// int newGame = 1;
	
	// window
	int boardwidth = 800;
	int boardHeight = 600;
	
	
	int cardWidth = 110; // ratio 1/1.4
	int cardHeight = 154;
	
	JFrame frame = new JFrame("Black Jack");
	
	
	JPanel gamePanel = new JPanel() {
		@Override
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			try {
				// draw hidden card
				Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
				if (!stayButton.isEnabled() ) {
					
					hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())) .getImage();
				}
				g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);
				
				// draw dealer's hand
				for (int i = 0; i < dealerHand.size(); i++) {
					Card card = dealerHand.get(i);
					Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
					g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
				}
				
				// draw playe'r Hand
				for (int i = 0; i < playerHand.size(); i++) {
					Card card = playerHand.get(i);
					Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
					g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
					
				}
				
				if (!stayButton.isEnabled() && !placeBetButton.isEnabled()) {
					dealerSum = reduceDealerAce();
					playerSum = reducePlayerAce();
					System.out.println("Stay: ");
					System.out.println(dealerSum);
					System.out.println(playerSum);
					System.out.println("Handsize"+playerHand.size());
					String message = "";
					
					System.out.println(totalMoney);
					if((playerAceCount==2) && (playerHand.size()==2)){
						message = "Known Bug AA";
					} else if (playerSum > 21) {
						message = "You Lose!";
						totalMoney -= currentBet;
					} else if (dealerSum > 21) {
						message = "You Win!";
						totalMoney += currentBet;
					} else if (playerSum == dealerSum) {
						message = "Tie!";
					} else if (playerSum > dealerSum) {
						message = "You Win!";
						totalMoney += currentBet;
					} else if (playerSum < dealerSum) {
						message = "You Lose!";
						totalMoney -= currentBet;
					}
					
					System.out.println(totalMoney);
					g.setFont(new Font("Arial", Font.PLAIN, 30));
					g.setColor(Color.white);
					g.drawString(message, 350, 250);
					
					totalLabel.setText(winningOrLoses(totalMoney) + totalMoney);
					nextButton.setEnabled(true);
				}
				
			} catch (Exception e) {
				System.out.println("AAAANull");
				//e.printStackTrace(); //Commented this out to better trouble shoot ACES Bug
			}
		}
	};
	JPanel buttonPanel = new JPanel();
	JButton hitButton = new JButton("Hit");
	JButton stayButton = new JButton(stayOrBust(playerSum));
	JButton nextButton = new JButton("Next Game");
	JTextField betsField = new JTextField(10);
	JLabel totalLabel = new JLabel("Total: " + "$0");
	int totalMoney = 0;
	int currentBet= 0;
	JButton placeBetButton = new JButton("Place Bet");
	
	
	
	
	public BlackJack() {
		// startGame();  //I Commented this out to enable next game feaure 
		frame.setVisible(true);
		frame.setSize(boardwidth, boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gamePanel.setLayout(new BorderLayout());
		gamePanel.setBackground(new Color(53, 101, 77));
		frame.add(gamePanel);
		
		hitButton.setFocusable(false);
		buttonPanel.add(hitButton);
		hitButton.setEnabled(false);
		stayButton.setFocusable(false);
		stayButton.setEnabled(false);
		buttonPanel.add(stayButton);
		nextButton.setFocusable(false);
		buttonPanel.add(nextButton);
		nextButton.setEnabled(false);
		buttonPanel.add(new JLabel("Bet:"));
		buttonPanel.add(betsField);
		betsField.requestFocusInWindow();
		buttonPanel.add(placeBetButton);
		frame.getRootPane().setDefaultButton(placeBetButton);
		buttonPanel.add(totalLabel);
		
		frame.add(buttonPanel, BorderLayout.SOUTH);
		
		placeBetButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					currentBet= Integer.parseInt(betsField.getText());
					if (currentBet> 0 && currentBet<10000) {
						startGame();
						hitButton.setEnabled(true);
						stayButton.setEnabled(true);
						placeBetButton.setEnabled(false);
					} else {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(frame, "Please enter a bet under 10K.", "Invalid Bet", JOptionPane.ERROR_MESSAGE);
					betsField.setEnabled(true);
				}
			}
		});
		
		
		hitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Card card = deck.remove(deck.size() - 1);
				playerSum += card.getValue();
				playerAceCount += card.isAce() ? 1 : 0;
				playerHand.add(card);
				if (reducePlayerAce() > 21) { // A + 2 + j --> 1 + 2 + J
					hitButton.setEnabled(false);
					
				}
				stayButton.setText(stayOrBust(playerSum));
				
				gamePanel.repaint();
			}
		});
		
		stayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hitButton.setEnabled(false);
				stayButton.setEnabled(false);
				
				while (dealerSum <= 17) {
					Card card = deck.remove(deck.size() - 1);
					dealerSum += card.getValue();
					dealerAceCount += card.isAce() ? 1 : 0;
					dealerHand.add(card);
				}
				gamePanel.repaint();
			}
		});
		
		gamePanel.repaint();
		
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextButton.setEnabled(false);
				betsField.setEnabled(true);
				betsField.requestFocusInWindow();
				placeBetButton.setEnabled(true);
				playerHand = new ArrayList<Card>();
				dealerHand= new ArrayList<Card>();
				try {
				hiddenCard = null;
			} catch (NullPointerException ex) {
				System.out.println("shhhhhhh");
			}
				
				gamePanel.repaint();
			}
		});
	}
	
	public void startGame() {
		betsField.setEnabled(false);
		// deck
		builDeck();
		shuffleDeck();
		
		// dealer
		dealerHand = new ArrayList<Card>();
		dealerSum = 0;
		dealerAceCount = 0;
		
		hiddenCard = deck.remove(deck.size() - 1); // remove card at last Index
		dealerSum += hiddenCard.getValue();
		dealerAceCount += hiddenCard.isAce() ? 1 : 0;
		Card card = deck.remove(deck.size() - 1);
		dealerSum += card.getValue();
		dealerAceCount += card.isAce() ? 1 : 0;
		dealerHand.add(card);
		if (dealerAceCount == 2 && dealerHand.size() == 1) { // This catches the ace before it busts the 21 limit
			dealerSum -= 10;
			dealerAceCount-=1; 
		}
		
		System.out.println("DEALER");
		System.out.println(hiddenCard);
		System.out.println(dealerHand);
		System.out.println(dealerSum);
		System.out.println(dealerAceCount);
		
		// player
		playerHand = new ArrayList<Card>();
		playerSum = 0;
		playerAceCount = 0;
		
		for (int i = 0; i < 2; i++) {
			card = deck.remove(deck.size() - 1);
			playerSum += card.getValue();
			playerAceCount += card.isAce() ? 1 : 0;
			playerHand.add(card);
		}
		if (playerAceCount == 2 && playerHand.size() == 2) { // This catches the ace before it busts the 21 limit
			playerSum -= 10; 
			dealerAceCount-=1; 
		}

		System.out.println("PLAYER: ");
		System.out.println(playerHand);
		System.out.println(playerSum);
		System.out.println(playerAceCount);
		
		stayButton.setText(stayOrBust(playerSum));
		betsField.setText("");
		
		gamePanel.repaint();
		
	}
	
	
	public void builDeck() {
		deck = new ArrayList<Card>();
		String[] values = { "A" , "2" , "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
		String[] types = { "C", "D", "H", "S"};
		
		for (int i = 0; i < types.length; i++) {
			
			for (int j = 0; j < values.length; j++) {
				Card card = new Card(values[j], types[i]);
				deck.add(card);
			}
		}
		System.out.println("Build Deck;");
		System.out.println(deck);
	}
	
	public void shuffleDeck() {
		for (int i = 0; i < deck.size(); i++) {
			int j = random.nextInt(deck.size());
			Card currCard = deck.get(i);
			Card randomCard = deck.get(j);
			deck.set(i, randomCard);
			deck.set(j, currCard);
		}
		
		System.out.println("AFTER Shuffle");
		System.out.println(deck);
	}
	public int reducePlayerAce() {
	
		while ((playerSum > 21 && playerAceCount > 0)){
			playerSum -= 10;
			playerAceCount -= 1;
		}
		return playerSum;
	}
	
	public int reduceDealerAce() {
		while (dealerSum > 21 && dealerAceCount > 0) {
			dealerSum -= 10;
			dealerAceCount -= 1;
		}
		return dealerSum;
	}
	
	public String stayOrBust(int playerScore){
		if((playerScore>21) && (playerHand.size()!=2)){
			return "Bust!";
		}
		return "Stay";
		
	}

	public String winningOrLoses(int owed){
		if(owed == 0){
			return " ";
		}else if(owed > 0){
			return "Winnings: $";
		}else 
		return "Loss: $";
		
	}
}
