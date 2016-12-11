/*
 * Project 1
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Object.*;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


public class readImage
{
	int imageCount = 1;
	double intensityBins [] = new double [26];
	double intensityMatrix [][] = new double[100][26];
	double colorCodeBins [] = new double [64];
	double colorCodeMatrix [][] = new double[100][64];

	/*Each image is retrieved from the file.  The height and width are found for the image and the getIntensity and
	 * getColorCode methods are called.
	 */
	public readImage()
	{
		while(imageCount < 101){
			try
			{
				// the line that reads the image file
				///////////////////  
				///your code///
				//////////////////
				
				File f = new File(getClass().getResource("images/" + imageCount + ".jpg").toURI());
				//Using BufferedImage to get image features.
				BufferedImage img = ImageIO.read(f);
				int height = img.getHeight();
				int width = img.getWidth();
				getIntensity(img, height, width);
				getColorCode(img, height, width);
				imageCount++;
			} 
			catch (IOException | URISyntaxException e)
			{
				System.out.println("Error occurred when reading the file.");
			}
		}

		writeIntensity();
		writeColorCode();

	}

	//intensity method 

	public void getIntensity(BufferedImage image, int height, int width){

		/////////////////////
		///your code///
		/////////////////
		
		Color color = null;
		int intensity = 0;
		
		//All dimensions are placed in the first part of the row.
		intensityMatrix[imageCount - 1][0] = height * width;
		
		//Fills proper intensity bins with image attributes.
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				color = new Color(image.getRGB(j, i));
				//Intensity equation
				intensity = (int) (.299*color.getRed() + .587*color.getGreen()
						+ 0.114*color.getBlue());
				if (intensity < 250) {
					intensityMatrix[imageCount - 1][(int) (intensity/10) + 1]++;
				}
				else {
					intensityMatrix[imageCount - 1][25]++;
				}
			}
			
		}
	}

	//color code method
	public void getColorCode(BufferedImage image, int height, int width){
		/////////////////////
		///your code///
		/////////////////
		
		Color color = null;
		int colorCode = 0;
		
		//Fills proper colorCode bins with image attributes.
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				color = new Color(image.getRGB(j, i));
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();

				/*
				 * Uses a private method to get the value of the color into binary form
				 * then gets the first 2 characters of each color.
				 */
				
				colorCode = Integer.parseInt(ColorToBinary(red).substring(0, 2) + 
						ColorToBinary(green).substring(0, 2) +
						ColorToBinary(blue).substring(0, 2), 2);
				
				
				colorCodeMatrix[imageCount - 1][colorCode]++;

			}
			
		}
	}


	///////////////////////////////////////////////
	//add other functions you think are necessary//
	///////////////////////////////////////////////
	
	/*
	 * Takes the integer value of the color and converts it to binary with leading
	 * and following zeros and returns it in a String.
	 */
	private String ColorToBinary(int color) {
		StringBuilder line = new StringBuilder(Integer.toBinaryString(color));
		while(line.length() < 8) {
			line.insert(0, '0');
		}
		return line.toString();
	}
	
	//This method writes the contents of the colorCode matrix to a file named colorCodes.txt.
	public void writeColorCode() {
		/////////////////////
		///your code///
		/////////////////
		
		File file;
		BufferedWriter writer = null;
		try {

			URI url = new URI(getClass().getResource("") + "colorCodes.txt");
			file = new File(url);
			
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			writer = new BufferedWriter(new FileWriter(file));
			
			//Goes through the colorCodeMatrix and divides each attribute with a comma
			//Also ensures no comma before the first value or after the last value.
			String comma = "";
			for(double[] i: colorCodeMatrix) {
				for(double j: i) {
					writer.write(comma + String.valueOf((int) j));
					comma = ",";
				}
			}

			writer.close();
		}

		catch (IOException | URISyntaxException e) {

			System.out.println("File could not be created." + e);
		}

	}

	//This method writes the contents of the intensity matrix to a file called intensity.txt
	public void writeIntensity(){
		/////////////////////
		///your code///
		/////////////////
		File file;
		BufferedWriter writer = null;
		
		try {
			URI url = new URI(getClass().getResource("") + "intensity.txt");
			file = new File(url);

			if(!file.exists()) {
				file.createNewFile();
			}
			
			writer = new BufferedWriter(new FileWriter(file));
			
			//Goes through the intensityMatrix and divides each attribute with a comma
			//Also ensures no comma before the first value or after the last value.
			String comma = "";
			for(double[] i: intensityMatrix) {
				for(double j: i) {
					writer.write(comma + String.valueOf((int) j));
					comma = ",";
				}
			}
			
			writer.close();
		}

		catch (IOException | URISyntaxException e) {

			System.out.println("File could not be created.");
		}
	}

	public static void main(String[] args)
	{
		new readImage();
	}

}
