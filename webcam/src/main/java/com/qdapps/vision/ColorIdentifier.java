package com.qdapps.vision;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class ColorIdentifier {

	private int [] rgb = new int [3];
	private int tolerance = 50;
	public int getTolerance() {
		return tolerance;
	}

	public void setTolerance(int tolerance) {
		this.tolerance = tolerance;
	}

	public ColorIdentifier(int r, int g, int b) {
		rgb = new int[]{r,g,b};
	}

	public Rectangle2D [] identfy(){
		Rectangle2D rec = new Rectangle(0,0,10,10);
		
		return null;
	}

	public  BufferedImage identfy(BufferedImage img) {
		
		List<Rectangle> l = new LinkedList<>(); 
		Rectangle r = null;
		
		if (img == null)return null;
		
		int width = img.getWidth();
		int hight = img.getHeight();
		
		//tag color to a array 
		BufferedImage  colorGrp = new BufferedImage(width, hight, BufferedImage.TYPE_4BYTE_ABGR);
		int argbColor = 0xC0FF0000;
		
		for (int i = 0; i< width; i++){
			for (int j = 0; j< hight; j++){
				//if (i,j) in rgb range; tag;
				int pixRgb = img.getRGB(i, j);
				
				//sint alpha = (pixRgb >> 24) & 0xFF;
				int red =   (pixRgb >> 16) & 0xFF;
				int green = (pixRgb >>  8) & 0xFF;
				int blue =  (pixRgb      ) & 0xFF;
				if (green- red>14 && green - blue >14){
					
					colorGrp.setRGB(i, j, argbColor);
					if (r== null){
						r= new Rectangle(i,j,0,0);
					}else {
						r.setSize(i-r.getLocation().x, j-r.getLocation().y);
					}
					
				}
				
				
			}
		}
		
		
		//return new Rectangle []{r};
		return colorGrp;
	}

	private boolean match(int red, int i) {
		
		if (Math.abs(red - i )<tolerance) return true;
		return false;
	}
	
}
