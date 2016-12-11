/* Project 1
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.*;

public class CBIR extends JFrame{

	private JLabel photographLabel = new JLabel();  //container to hold a large 
	private JButton [] button; //creates an array of JButtons
	private int [] buttonOrder = new int [101]; //creates an array to keep up with the image order
	private double [] imageSize = new double[101]; //keeps up with the image sizes
	private GridLayout gridLayout1;
	private GridLayout gridLayout2;
	private GridLayout gridLayout3;
	private GridLayout gridLayout4;
	private JPanel panelBottom1;
	private JPanel panelTop1;
	private JPanel buttonPanel1;
	private Double [][] intensityMatrix = new Double [100][26];
	private Double [][] colorCodeMatrix = new Double [100][64];
	private Map <Double , LinkedList<Integer>> map;
	int picNo = 0;
	int imageCount = 1; //keeps up with the number of images displayed since the first page.


	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CBIR app = new CBIR();
				app.setVisible(true);
			}
		});
	}



	public CBIR() {
		//The following lines set up the interface including the layout of the buttons and JPanels.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Icon Demo: Please Select an Image");        
		panelBottom1 = new JPanel();
		panelTop1 = new JPanel();
		buttonPanel1 = new JPanel();
		gridLayout1 = new GridLayout(4, 5, 5, 5);
		gridLayout2 = new GridLayout(2, 1, 5, 5);
		gridLayout3 = new GridLayout(1, 2, 5, 5);
		gridLayout4 = new GridLayout(2, 3, 5, 5);
		setLayout(gridLayout2);
		panelBottom1.setLayout(gridLayout1);
		panelTop1.setLayout(gridLayout3);
		add(panelTop1);
		add(panelBottom1);
		photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
		photographLabel.setHorizontalTextPosition(JLabel.CENTER);
		photographLabel.setHorizontalAlignment(JLabel.CENTER);
		photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel1.setLayout(gridLayout4);
		panelTop1.add(photographLabel);
		panelTop1.add(buttonPanel1);

		/*HTML is used to add spaces on the button text */
		
		JButton previousPage = new JButton("<html>View Previous<br />20 Pictures<html>");
		JButton nextPage = new JButton("<html>View Next<br />20 Pictures<html>");
		JButton intensity = new JButton("<html>Query Images<br />By Intensity<html>");
		JButton colorCode = new JButton("<html>Query Images<br />By Color Code<html>");
		JButton returnOrder = new JButton("<html>Return Pictures<br />to Original Order<html>");
		JButton intenColorCode = new JButton("<html>Query Images<br />By Intensity + Color Code<html>");
		
		/*Buttons must be added in this order for the correct display*/
		
		buttonPanel1.add(intensity);
		buttonPanel1.add(colorCode);
		buttonPanel1.add(intenColorCode);
		buttonPanel1.add(previousPage);
		buttonPanel1.add(returnOrder);
		buttonPanel1.add(nextPage);
		

		nextPage.addActionListener(new nextPageHandler());
		previousPage.addActionListener(new previousPageHandler());
		intensity.addActionListener(new intensityHandler());
		colorCode.addActionListener(new colorCodeHandler());
		
		//Added for addition features
		returnOrder.addActionListener(new returnOrderHandler());
		intenColorCode.addActionListener(new intenColorCodeHandler());
		setSize(1100, 750);
		// this centers the frame on the screen
		setLocationRelativeTo(null);


		button = new JButton[101];
		/*This for loop goes through the images in the database and stores them as icons and adds
		 * the images to JButtons and then to the JButton array
		 */
		for (int i = 1; i < 101; i++) {
			ImageIcon icon;
			icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
			if(icon != null){;
				button[i] = new JButton(icon);
				button[i].addActionListener(new IconButtonHandler(i, icon));
				buttonOrder[i] = i;
			}
		}

		readIntensityFile();
		readColorCodeFile();
		displayFirstPage();
	}

	/*This method opens the intensity text file containing the intensity matrix with the histogram bin values for each image.
	 * The contents of the matrix are processed and stored in a two dimensional array called intensityMatrix.
	 */
	public void readIntensityFile(){
		Scanner read = null;
		try{
			read =new Scanner(new File (getClass().getResource("intensity.txt").toURI()));
			read.useDelimiter(",");
			/////////////////////
			///your code///
			/////////////////

			
			/* loops through the files and constructs a matrix with
			 * intensity values.
			 */
			for(int i = 0; i < intensityMatrix.length; i++) {
				for(int j = 0; j < intensityMatrix[0].length; j++) {
					intensityMatrix[i][j] = read.nextDouble();
				}
				imageSize[i] = intensityMatrix[i][0];
			}
		}
		catch(FileNotFoundException | URISyntaxException EE){
			System.out.println("The file intensity.txt does not exist");
		}
		finally {
			read.close();
		}
	}

	/*This method opens the color code text file containing the color code matrix with the histogram bin values for each image.
	 * The contents of the matrix are processed and stored in a two dimensional array called colorCodeMatrix.
	 */
	private void readColorCodeFile(){
		Scanner read = null;
		try{
			read =new Scanner(new File (getClass().getResource("colorCodes.txt").toURI()));
			read.useDelimiter(",");
			/////////////////////
			///your code///
			/////////////////

			
			/* loops through the files and constructs a matrix with
			 * colorCode values.
			 */
			for(int i = 0; i < colorCodeMatrix.length; i++) {
				for(int j = 0; j < colorCodeMatrix[0].length; j++) {
					colorCodeMatrix[i][j] = read.nextDouble();
				}
			}
		}
		catch(FileNotFoundException | URISyntaxException EE){
			System.out.println("The file intensity.txt does not exist");
		}
		finally {
			read.close();
		}
	}

	/*This method displays the first twenty images in the panelBottom.  The for loop starts at number one and gets the image
	 * number stored in the buttonOrder array and assigns the value to imageButNo.  The button associated with the image is 
	 * then added to panelBottom1.  The for loop continues this process until twenty images are displayed in the panelBottom1
	 */
	private void displayFirstPage(){
		int imageButNo = 0;
		//added this to make sure every time this method is called imageCount is 1
		imageCount = 1;
		panelBottom1.removeAll(); 
		for(int i = 1; i < 21; i++){
			imageButNo = buttonOrder[i];
			panelBottom1.add(button[imageButNo]);
			
			imageCount ++;
		}
		
		/*This was added to set the next and previous buttons
		 * to grey out when they cannot be used.
		 */
		buttonPanel1.getComponent(3).setEnabled(false);
		if(imageCount > 20) {
			buttonPanel1.getComponent(5).setEnabled(true);
		}
		panelBottom1.revalidate();  
		panelBottom1.repaint();

	}

	/*This class implements an ActionListener for each iconButton.  When an icon button is clicked, the image on the 
	 * the button is added to the photographLabel and the picNo is set to the image number selected and being displayed.
	 */ 
	private class IconButtonHandler implements ActionListener{
		int pNo = 0;
		ImageIcon iconUsed;

		IconButtonHandler(int i, ImageIcon j){
			pNo = i;
			iconUsed = j;  //sets the icon to the one used in the button
		}

		public void actionPerformed( ActionEvent e){
			photographLabel.setIcon(iconUsed);
			//Added to show picture name under the photo
			photographLabel.setText(String.valueOf(pNo) + ".jpg");
			picNo = pNo;
		}

	}

	/*This class implements an ActionListener for the nextPageButton.  The last image number to be displayed is set to the 
	 * current image count plus 20.  If the endImage number equals 101, then the next page button does not display any new 
	 * images because there are only 100 images to be displayed.  The first picture on the next page is the image located in 
	 * the buttonOrder array at the imageCount
	 */
	private class nextPageHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			int imageButNo = 0;
			int endImage = imageCount + 20;
			if(endImage <= 101){
				panelBottom1.removeAll(); 
				for (int i = imageCount; i < endImage; i++) {
					imageButNo = buttonOrder[i];
					panelBottom1.add(button[imageButNo]);
					imageCount++;

				}

				panelBottom1.revalidate();  
				panelBottom1.repaint();
			}
			/*This was added to set the next and previous buttons
			 * to grey out when they cannot be used.
			 */
			if(imageCount > 20) {
				buttonPanel1.getComponent(3).setEnabled(true);
			}
			if(imageCount > 99) {
				buttonPanel1.getComponent(5).setEnabled(false);
			}
		}

	}

	/*This class implements an ActionListener for the previousPageButton.  The last image number to be displayed is set to the 
	 * current image count minus 40.  If the endImage number is less than 1, then the previous page button does not display any new 
	 * images because the starting image is 1.  The first picture on the next page is the image located in 
	 * the buttonOrder array at the imageCount
	 */
	private class previousPageHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			int imageButNo = 0;
			int startImage = imageCount - 40;
			int endImage = imageCount - 20;
			if(startImage >= 1){
				panelBottom1.removeAll();
				/*The for loop goes through the buttonOrder array starting with the startImage value
				 * and retrieves the image at that place and then adds the button to the panelBottom1.
				 */
				for (int i = startImage; i < endImage; i++) {
					imageButNo = buttonOrder[i];
					panelBottom1.add(button[imageButNo]);
					imageCount--;

				}
				buttonPanel1.getComponent(5).setEnabled(true);
				panelBottom1.revalidate();  
				panelBottom1.repaint();
			}
			/*This was added to set the next and previous buttons
			 * to grey out when they cannot be used.
			 */
			if(startImage == 1) {
				buttonPanel1.getComponent(3).setEnabled(false);
			}
		}

	}

	/*This methods implements an action listener to reorder the elements
	 * based on the order that they were first displayed in.
	 */
	private class returnOrderHandler implements ActionListener {

		public void actionPerformed( ActionEvent e) {
			for (int i = 1; i < buttonOrder.length; i++) {
				buttonOrder[i] = i;
			}

			displayFirstPage();
			
			/*This was added to set the next and previous buttons
			 * to grey out when they cannot be used.
			 */
			buttonPanel1.getComponent(5).setEnabled(true);
		}
	}


	/*This class implements an ActionListener when the user selects the intensityHandler button.  The image number that the
	 * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
	 * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one.
	 * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
	 * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
	 * The images are then arranged from most similar to the least.
	 */
	private class intensityHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			double [] distance = new double [101];
			map = new HashMap<Double, LinkedList<Integer>>();
			int pic = (picNo - 1);
			int startIndex = 1;
			if(pic == -1) {
				photographLabel.setText("Please select an image to query.");
			}
			else {

				/////////////////////
				///your code///
				/////////////////

				
				//These methods are at the bottom.
				
				distance = findDistance(intensityMatrix, startIndex);
				sortByDistance(distance);
				
				/*
				 * Puts all of the pictures and order by distance using
				 * the displayFirstPage method.
				 */
				displayFirstPage();

			}
		}
	}

	/*This class implements an ActionListener when the user selects the colorCode button.  The image number that the
	 * user would like to find similar images for is stored in the variable pic.  pic takes the image number associated with
	 * the image selected and subtracts one to account for the fact that the intensityMatrix starts with zero and not one. 
	 * The size of the image is retrieved from the imageSize array.  The selected image's intensity bin values are 
	 * compared to all the other image's intensity bin values and a score is determined for how well the images compare.
	 * The images are then arranged from most similar to the least.
	 */ 
	private class colorCodeHandler implements ActionListener{

		public void actionPerformed( ActionEvent e){
			double [] distance;
			map = new HashMap<Double, LinkedList<Integer>>();
			int pic = (picNo - 1);
			int startIndex = 0;
			if(pic == -1) {
				photographLabel.setText("Please select an image to query.");
			}
			else {
				/////////////////////
				///your code///
				/////////////////
				distance = findDistance(colorCodeMatrix, startIndex);
				
				sortByDistance(distance);
				
				/*
				 * Puts all of the pictures and order by distance using
				 * the displayFirstPage method.
				 */
				displayFirstPage();
			}
		}
	}
	
	/*
	 * This method blends both the intensity and colorCode query. The distances of
	 * both methods are summed together and placed in a map. Then the distance array
	 * is passed to the sortByDistance method.
	 */
	private class intenColorCodeHandler implements ActionListener {
		public void actionPerformed( ActionEvent e){
			double [] intensityDistance;
			double [] colorCodeDistance;
			double [] distance = new double[101];
			map = new HashMap<Double, LinkedList<Integer>>();
			int pic = (picNo - 1);
			int intenStartIndex = 1;
			int colorStartIndex = 0;
			if(pic == -1) {
				photographLabel.setText("Please select an image to query.");
			}
			else {
				/////////////////////
				///your code///
				/////////////////
				
				intensityDistance = findDistance(intensityMatrix, intenStartIndex);
				colorCodeDistance = findDistance(colorCodeMatrix, colorStartIndex);
				
				map.clear();
				
				for(int i = 1; i < distance.length; i++) {
					distance[i] = intensityDistance[i] + colorCodeDistance[i];
					
					if(map.containsKey(distance[i])) {
						map.get(distance[i]).add(i);
					}
					else {
						LinkedList<Integer> listStart = new LinkedList<Integer>();
						listStart.add(i);
						map.put(distance[i], listStart);
					}
				}
				
				sortByDistance(distance);
				
				/*
				 * Puts all of the pictures and order by distance using
				 * the displayFirstPage method.
				 */
				displayFirstPage();
			}
		}
	}
	
	/*
	 * This method takes a matrix and the index in that matrix where picture features
	 * start, it then calculates the distance from each picture, and adds the value of the
	 * picture index to a Hashmap with the distance as the key. It returns a Double Array
	 * that contains all of the distances.
	 */
	private double[] findDistance(Double[][] matrix, int picFeatureStartIndex) {
		double[] result = new double[matrix.length + 1];
		int compareImage = 1;
		int pic = (picNo - 1);
		int picFeature;
		double d = 0;
		try {
		double picSize = imageSize[pic];
		while(compareImage < 101) {
			d = 0;
			picFeature = picFeatureStartIndex;
			while(picFeature < matrix[0].length) {
				d += Math.abs((matrix[pic][picFeature]/picSize)
						-(matrix[compareImage - 1][picFeature]/intensityMatrix[compareImage - 1][0]));
				picFeature++;
			}
			result[compareImage] = d;
			if(map.containsKey(d)) {
				map.get(d).add(compareImage + 1);
			}
			else {
				LinkedList<Integer> listStart = new LinkedList<Integer>();
				listStart.add(compareImage);
				map.put(d, listStart);
			}
			compareImage++;
		}
		} catch (ArrayIndexOutOfBoundsException e){
			photographLabel.setText("Please select an image to query.");
		}
		
		return result;
	}
	
	/*
	 * This method takes a double array and sorts it, then iterates through the
	 * that array to take values from a map and put them in a LinkedHashSet, ordered
	 * by distance, then it reorders the buttons that contain the pictures.
	 */
	private void sortByDistance(double[] distance) {
		Arrays.sort(distance);
		LinkedHashSet<Integer> ordered = new LinkedHashSet<Integer>();
		int i = 1;
		while(i < distance.length) {
			ordered.addAll(map.get(distance[i]));
			i++;
		}
		i = 1;
		for(int j: ordered) {
			buttonOrder[i] = j;
			i++;
		}	
	}
}
