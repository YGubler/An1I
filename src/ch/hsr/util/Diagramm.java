package ch.hsr.util;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Diagramm extends JPanel {

	double xMin = 0;
	double xMax = 0;
	double yMin = 0;
	double yMax = 0;
	Graphics graphics;
	
	public static class Rectangle{
		double x,X,y,Y;
		public Rectangle(double xArg, double yArg, double XArg, double YArg) {
			x = xArg;
			y = yArg;
			X = XArg;
			Y = YArg;
		}
	}
	
	public interface Feature {
		public Rectangle getDimensions();	
		public void draw(Diagramm diag);
	}
	
	public interface Polygon extends Feature{
		public void setColor(Color c);
		public void addPoint(double x, double y);
	}
	
	public static class Point implements Feature {
		Color col;
		double xArg;
		double yArg;
		
		public Point(Color c, double x, double y){
			col = c;
			xArg = x;
			yArg = y;
		}
		public Rectangle getDimensions(){
			return new Rectangle(xArg,yArg,xArg,yArg);
		}
		public void draw(Diagramm diag){
			diag.setColor(col);
			diag.drawPoint(xArg, yArg);
		}
		
	}
	
	public static class PolygonImpl implements Polygon {
		
		Color col;
		Rectangle rect;
		
		private static class Point{
			public double x;
			public double y;
			
			Point(double x_, double y_){
				x = x_;
				y = y_;
			}
		}
		
		List<Point> points;
		
		PolygonImpl(){
			col = Color.BLACK;
			rect = new Rectangle(0,0,0,0);
			points = new LinkedList<Point>();
		}

		public void setColor(Color c){
			col = c;
		}
		
		public void addPoint(double x, double y){
			if (rect.x > x) rect.x = x;
			if (rect.X < x) rect.X = x;
			if (rect.y > y) rect.y = y;
			if (rect.Y < y) rect.Y = y;
			
			points.add(new Point(x,y));
		}
		
		public Rectangle getDimensions(){
			return rect;
		}
		public void draw(Diagramm diag){
			diag.setColor(col);
			Point q = null;
			for (Point p : points){
				if (q == null) {
					q = p;
				} else {
					diag.drawLine(q.x, q.y, p.x, p.y);		
					q = p;
				}
			}
		}
		
		
	}
	

	List<Feature> list;
	
	public Diagramm(){
		super();
		list = new LinkedList<Feature>();
	}
	
	public void clear(){
		list = new LinkedList<Feature>();
		this.getGraphics().clearRect(0,0,this.getWidth(),this.getHeight());
		repaint();
	}
	
	public void addPoint(Color col, double xArg, double yArg){
		list.add(new Point(col,xArg,yArg));
	}
	
	public Polygon createPolygon(){
		PolygonImpl poly = new PolygonImpl();
		list.add(poly);
		return poly;
	}

	public void show(){
		repaint();
	}
	
	public void paint(Graphics g)
	{
	    super.paint(g);
	    graphics = g;
		xMin = 0;
		xMax = 0;
		yMin = 0;
		yMax = 0;
		if (graphics == null) return;
		graphics.clearRect(0,0,this.getWidth(),this.getHeight());
		
		for(Feature feature : list){
			Rectangle rect = feature.getDimensions();
			if (xMin > rect.x) xMin = rect.x;
			if (xMax < rect.X) xMax = rect.X;
			if (yMin > rect.y) yMin = rect.y;
			if (yMax < rect.Y) yMax = rect.Y;
		}
		
		for(Feature feature : list){
			feature.draw(this);
		}
		
		graphics.setColor(Color.BLACK);
		graphics.drawLine(calcX(0), calcY(yMin), calcX(0), calcY(yMax));
		graphics.drawLine(calcX(xMin), calcY(0), calcX(xMax), calcY(0));		
		graphics.drawString(String.format("%.2f", xMin), calcX(xMin), calcY(0));
		graphics.drawString(String.format("%.2f", xMax), calcX(xMax)-10, calcY(0));
		graphics.drawString(String.format("%.2f", yMin), calcX(0), calcY(yMin));
		graphics.drawString(String.format("%.2f", yMax), calcX(0), calcY(yMax)+5);
	}

	void setColor(Color col){
		graphics.setColor(col);		
	}
	
	int calcX(double xArg){
		return 5+(int)((this.getWidth()- 30) * (xArg - xMin ) / (xMax - xMin));
	}

	int calcY(double yArg){
		return this.getHeight() -5 - (int)((this.getHeight()- 20) * (yArg - yMin ) / (yMax - yMin));
	}

	void drawLine(double x, double y, double X, double Y){
		
		int x_ = calcX(x);
		int y_ = calcY(y);
		int X_ = calcX(X);
		int Y_ = calcY(Y);
		graphics.drawLine(x_, y_, X_, Y_);		
	}
	
	void drawOval(double x, double y, double xRad, double yRad){
		int x_ = calcX(x);
		int y_ = calcY(y);
		int xR = calcX(xRad) - calcX(0);
		int yR = calcY(yRad) - calcY(0);
		graphics.drawOval(x_-xR/2, y_-yR/2, xR, yR);
	}

	void drawPoint(double x, double y){
		int x_ = calcX(x);
		int y_ = calcY(y);
		graphics.drawOval(x_, y_, 0,0);			
	}

}
