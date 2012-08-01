package com.qdapps.vision;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ColorIdentifier {

	private int[] rgb = new int[3];
	private int tolerance = 50;
	private boolean useDebugImg = true;
	private int xStep = 10;
	private int yStep = 10;
	private double thredHold = 10.0;
	private int argbColor = 0xC0FF0000;

	public boolean isDebugImg() {
		return useDebugImg;
	}

	public void setDebugImg(boolean debugImg) {
		this.useDebugImg = debugImg;
	}

	public int getTolerance() {
		return tolerance;
	}

	public void setTolerance(int tolerance) {
		this.tolerance = tolerance;
	}

	public ColorIdentifier(int r, int g, int b) {
		rgb = new int[] { r, g, b };
	}

	public Rectangle2D[] identfy() {
		Rectangle2D rec = new Rectangle(0, 0, 10, 10);

		return null;
	}

	public Map<String, Object> identfy(BufferedImage img) {

		Map<String, Object> result = new Hashtable<>();

		if (img == null)
			return null;

		int width = img.getWidth();
		int hight = img.getHeight();

		// tag color to a array
		BufferedImage colorGrpImg = null;
		if (useDebugImg) {
			colorGrpImg = new BufferedImage(width, hight, BufferedImage.TYPE_4BYTE_ABGR);
		}

		List<Rectangle> recList = new LinkedList<>();

		for (int i = 0; i < width; i += xStep) {
			for (int j = 0; j < hight; j += yStep) {

				if (!inRectList(i,j, recList)){
					Rectangle rect = getRecByColor(img, i, j, colorGrpImg);
					if (rect != null) {
						recList = mereRectangle(recList, rect);
					}
				}
			}
		}

		if (colorGrpImg != null) {
			result.put(Cst.DebugImg, colorGrpImg);
		}
		result.put(Cst.RectangleList, recList);
		return result;
	}

	private boolean inRectList(int i, int j, List<Rectangle> recList) {
		for (Rectangle rec : recList){
			if (rec.contains(i, j)) return true;
		}
		return false;
	}

	private List<Rectangle> mereRectangle(List<Rectangle> recList, Rectangle rect) {
		List<Rectangle> newList = new LinkedList<>();
		boolean added = false;
		for (Rectangle rectangle : recList) {
			if (    inRect((int)rect.getMinX(), (int)rect.getMinY(), rectangle) ||
					inRect((int)rect.getMinX(), (int)rect.getMaxY(), rectangle) ||
					inRect((int)rect.getMaxX(), (int)rect.getMinY(), rectangle) ||
					inRect((int)rect.getMaxX(), (int)rect.getMaxY(), rectangle) ||
					inRect((int)rect.getCenterX(), (int)rect.getCenterY(), rectangle) 
					){
				newList.add( mergeRectangle (rectangle, rect));
				added = true;
			}else{
				newList.add(rectangle);
			}
			
		}
		
		if (!added){
			newList.add(rect);
		}
		
		return newList;
	}

	private Rectangle mergeRectangle(Rectangle r1, Rectangle r2) {
		int minX = (int) Math.min(r1.getMinX(), r2.getMinX());
		int minY = (int) Math.min(r1.getMinY(), r2.getMinY());
		int maxX = (int) Math.max(r1.getMaxX(), r2.getMaxX());
		int maxY = (int) Math.max(r1.getMaxY(), r2.getMaxY());
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	private Rectangle getRecByColor(BufferedImage img, int x, int y, BufferedImage colorGrpImg) {

		// if i,j in img matches;
		int pixRgb = img.getRGB(x, y);
		if (match(pixRgb)) {
			Rectangle matchRec = growRectangle(img, x, y, colorGrpImg);
			if (matchRec.getHeight() > thredHold && matchRec.getHeight() > thredHold) {
				return matchRec;
			}

		}

		return null;

	}

	private Rectangle growRectangle(BufferedImage img, int x, int y, BufferedImage colorGrpImg) {
		boolean growable = false;

		int[] gx = new int[] { x, x, x, x, x, x, x, x };
		int[] gy = new int[] { y, y, y, y, y, y, y, y };

		int[] dx = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
		int[] dy = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
		boolean[] stillGrowing = new boolean[] { true, true, true, true, true, true, true, true, };
		do {
			growable = false;
			for (int i = 0; i < dy.length; i++) {
				if (stillGrowing[i]) {
					stillGrowing[i] = false;
					for (int j = 5; j > 0; j--) {
						int tempx = gx[i] + j * dx[i];
						int tempy = gy[i] + j * dy[i];
						if (inRect(tempx, tempy, new Rectangle(0, 0, img.getWidth()-1, img.getHeight()-1))) {
							int pixRgb = img.getRGB(tempx, tempy);
							if (match(pixRgb)) {
								stillGrowing[i] = true;
								gx[i] = gx[i] + j * dx[i];
								gy[i] = gy[i] + j * dy[i];
								growable = true;
								if (colorGrpImg != null) {
									colorGrpImg.setRGB(gx[i], gy[i], argbColor);
								}
								break;
							}
						}
					}
				}
			}

		} while (growable);

		int maxX = -999;
		int maxY = -999;
		int minX = 99999;
		int minY = 99999;

		for (int i = 0; i < gx.length; i++) {
			maxX = Math.max(maxX, gx[i]);
			maxY = Math.max(maxY, gy[i]);
			minX = Math.min(minX, gx[i]);
			minY = Math.min(minY, gy[i]);
		}

		Rectangle r = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		return r;
	}

	private boolean inRect(int x,int y, Rectangle rectangle) {
		return rectangle.contains(x,y);
	}

	private boolean match(int pixRgb) {

		// sint alpha = (pixRgb >> 24) & 0xFF;
		int red = (pixRgb >> 16) & 0xFF;
		int green = (pixRgb >> 8) & 0xFF;
		int blue = (pixRgb) & 0xFF;
		if (green - red > 14 && green - blue > 14) {

			return true;

			// colorGrpImg.setRGB(i, j, argbColor);
			// if (r == null) {
			// r = new Rectangle(i, j, 0, 0);
			// } else {
			// r.setSize(i - r.getLocation().x, j - r.getLocation().y);
			// }

		}
		return false;
	}

}
