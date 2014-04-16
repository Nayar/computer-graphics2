/*
    Copyright 2013 by Nayar Joolfoo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MeraJFrame extends JFrame {
	Point origin;
	int[] scales = {1,2,5,10,25,50};
	int posScaleX = 5;
	int posScaleY = 5;
	boolean scaleBoth = true;
	boolean scaleDown = false;
	int xScale = scales[posScaleX];
	int yScale = scales[posScaleY];
	JPanel axisArea;
	int resized = 0;
	Graphics2D g2d;
	ArrayList<MeraPoint> points;
	boolean drawn = false;
	MouseListener ml;
	double[][] polygonMatrix = null;
	double[][] polygon1Matrix = null;
	ArrayList<double[][]> polygonMatrixIntermediates = new ArrayList<>();
	int angleRem = 0;
	Point rotatePoint;
	int xTranslationRem = 0;
	int yTranslationRem = 0;
	boolean reflectOnX = false;
	boolean reflectOnY = false;
	boolean scaleShape = false;
	int scalePointX=0;
	int scalePointY=0;
	double scaleFactorX=0;
	double scaleFactorY=0;
	double shearOnXRem = 0;
	double shearOnXYref = 0;
	double shearOnYRem = 0;
	double shearOnYXref = 0;
	boolean repaintrequired = false;
	Shape poly1;
	
	
	
	JButton doneDrawingBut,clearBut,reflectXBut,reflectYBut,rotateBut,translationBut,scaleBut,shearOnX,shearOnY;
	
	public MeraJFrame() {
		points = new ArrayList();
		setLayout(null);
		JPanel buttonArea = new JPanel();
		axisArea = new MeraDrawingArea();
		buttonArea.setBounds(0, 0, 250, 760);
		buttonArea.setLayout(new FlowLayout());
		doneDrawingBut = new JButton("Done Drawing");
		clearBut = new JButton("Clear");
		reflectXBut = new JButton("Reflect on x axis");
		reflectYBut = new JButton("Reflect on y axis");
		rotateBut = new JButton("Rotate");
		translationBut = new JButton("Translate");
		scaleBut= new JButton("Scale");
		shearOnX = new JButton("Shear along X axis");
		shearOnY = new JButton("Shear along Y axis");
		
		
		buttonArea.add(doneDrawingBut);
		buttonArea.add(clearBut);
		buttonArea.add(reflectXBut);
		buttonArea.add(reflectYBut);
		
		buttonArea.add(rotateBut);
		buttonArea.add(translationBut);
		buttonArea.add(scaleBut);
		buttonArea.add(shearOnX);
		buttonArea.add(shearOnY);

		//buttonArea.add
		axisArea.setBounds(250, 0, 1368-250, 760);
		add(axisArea);
		add(buttonArea);
		ml = new MouseAdapter() {
			
			public void mousePressed(MouseEvent arg0) {
				points.add(new MeraPoint(arg0.getX(), arg0.getY()));
				g2d.draw(new Ellipse2D.Double(arg0.getX(), arg0.getY(),5,5));
				System.out.printf("x: %f, y: %f\n", shiftXRev(arg0.getX()),shiftYRev(arg0.getY()));
				repaint();
			}
		};
		ActionListener al = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(polygon1Matrix != null)
					polygonMatrixIntermediates.add(polygon1Matrix);
				
				if(e.getSource() == clearBut){
					points = new ArrayList();
					posScaleX = scales.length - 1;
					posScaleY = scales.length - 1;
					xScale = scales[posScaleX];
					yScale = scales[posScaleY];
					polygonMatrix = null;
					polygon1Matrix = null;
					poly1 = null;
					drawn = false;
					polygonMatrixIntermediates = new ArrayList<>();
					axisArea.addMouseListener(ml);
					//revalidate();
					//repaint();
				}
				
				else if (e.getSource() == doneDrawingBut){
					drawn = true;
					axisArea.removeMouseListener(ml);
					polygon1Matrix = null;
					System.out.print("Going to repaint from doneDrawing\n");
					
				}
				
				else if(e.getSource() == rotateBut){
					angleRem = Integer.valueOf(JOptionPane.showInputDialog("Enter degrees"));
					String str1 = JOptionPane.showInputDialog("Enter rotation point (x,y)");
					String[] str2 = str1.split(",");
					rotatePoint = new Point(Integer.valueOf(str2[0]), Integer.valueOf(str2[1]));
				}
				
				else if(e.getSource() == translationBut){
					String str1 = JOptionPane.showInputDialog("Enter translation vector (x,y)");
					String[] str2 = str1.split(",");
					xTranslationRem = Integer.valueOf(str2[0]); // Get value from textbox
					yTranslationRem = Integer.valueOf(str2[1]); // Get value from textbox
					System.out.printf("yRem %d: ",yTranslationRem);
				}
				
				else if(e.getSource() == reflectXBut){
					reflectOnX = true;
				}
				
				else if (e.getSource()== reflectYBut){
					reflectOnY =true;
				}
				
				else if(e.getSource()== scaleBut){
					String str1 = JOptionPane.showInputDialog("Enter scale point (x,y)");
					String[] str2 = str1.split(",");
					scalePointX=Integer.valueOf(str2[0]);
					scalePointY= Integer.valueOf(str2[1]);
					str1 = JOptionPane.showInputDialog("Enter scale Factor (x,y)");
					str2 = str1.split(",");
					scaleFactorX= Double.valueOf(str2[0]);
					scaleFactorY= Double.valueOf(str2[1]);
					scaleShape= true;
				}
				
				else if(e.getSource() == shearOnX){
					shearOnXRem = Double.valueOf(JOptionPane.showInputDialog("Enter amt"));
					shearOnXYref = Double.valueOf(JOptionPane.showInputDialog("Enter y ref"));
				}
				
				else if(e.getSource() == shearOnY){
					shearOnYRem = Double.valueOf(JOptionPane.showInputDialog("Enter amt"));
					shearOnYXref = Double.valueOf(JOptionPane.showInputDialog("Enter x ref"));
				}
				validate();
				repaint();
			}
		};
		axisArea.addMouseListener(ml);
		doneDrawingBut.addActionListener(al);
		clearBut.addActionListener(al);
		rotateBut.addActionListener(al);
		translationBut.addActionListener(al);
		reflectXBut.addActionListener(al);
		reflectYBut.addActionListener(al);
		scaleBut.addActionListener(al);
		shearOnX.addActionListener(al);
		shearOnY.addActionListener(al);
	}
	
	public class MeraDrawingArea extends JPanel {
		public void paintComponent(Graphics g){
			
			g2d = (Graphics2D) g;
			g2d.clearRect(0, 0, getWidth(), getHeight());
			Stroke normalStroke = g2d.getStroke();
			System.out.print("Paiting\n");
			if(drawn == false){
				drawAxis();
				g2d.setPaint(Color.BLACK);
				for(int i = 0;i<points.size();i++){
					g2d.draw(new Ellipse2D.Double(shiftX(points.get(i).x),shiftY(points.get(i).y),5,5));
				}
			}
			else{
				drawAxis();
				try {Thread.sleep(100);} catch (InterruptedException e) {}
				polygonMatrix = new double[3][points.size()];
				for(int i = 0;i<points.size();i++){
					polygonMatrix[0][i] = points.get(i).x;
					polygonMatrix[1][i] = points.get(i).y;
					polygonMatrix[2][i] = 1;
				}
				
				g2d.setPaint(Color.BLUE);
				Shape poly = new MeraPolygon(polygonMatrix[0],polygonMatrix[1],polygonMatrix[0].length);
				g2d.draw(poly);
				g2d.fill(poly);
				if(polygon1Matrix == null)
					polygon1Matrix = polygonMatrix.clone();
				Color col = new Color(0,0,255);
				for(int i = polygonMatrixIntermediates.size() - 1;i>=0;i--){
					col = col.brighter();
					g2d.setPaint(col);
					Shape temp = new MeraPolygon(polygonMatrixIntermediates.get(i)[0],polygonMatrixIntermediates.get(i)[1],polygonMatrixIntermediates.get(i)[0].length);
					g2d.draw(temp);
					//g2d.fill(temp);
				}
				
				if(poly1 != null){
					g2d.setPaint(Color.RED);
					
					g2d.draw(poly1);
					g2d.fill(poly1);
				}
				
				drawAxis();
				translateShapes();
				reflectShape();
				//rotatePoint = new Point(0, 0);
				//angleRem++;
				scaleShape();
				rotateShapes();
				shear();
				
				
			}
		}
        
		private void shear() {
			if(shearOnXRem > 0){
				polygon1Matrix = MeraTransformer.shearX(0.5, 0, polygon1Matrix);
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				shearOnXRem -= 0.5;
				validate();
				repaint();
			}
			
			else if(shearOnXRem < 0){
				polygon1Matrix = MeraTransformer.shearX(-0.5, 0, polygon1Matrix);
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				shearOnXRem += 0.5;
				validate();
				repaint();
			}
			
			else if(shearOnYRem > 0){
				polygon1Matrix = MeraTransformer.shearY(0.5, 0, polygon1Matrix);
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				shearOnYRem -= 0.5;
				validate();
				repaint();
			}
			
			else if(shearOnYRem < 0){
				polygon1Matrix = MeraTransformer.shearY(-0.5, 0, polygon1Matrix);
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				shearOnYRem += 0.5;
				validate();
				repaint();
			}
			
		}

		private void reflectShape() {
			if(reflectOnX == true){
				polygon1Matrix = MeraTransformer.reflectOnXAxis(polygon1Matrix);
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				reflectOnX = false;
				validate();
				repaint();
			}
			else if (reflectOnY== true){
				polygon1Matrix = MeraTransformer.reflectOnYAxis(polygon1Matrix);
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				reflectOnY=false;
				validate();
				repaint();
			}
		}

		private void translateShapes() {
			if(xTranslationRem > 0){
				polygon1Matrix = MeraTransformer.translate(1,0,polygon1Matrix);
				xTranslationRem--;
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				validate();
				repaint();
			}
			else if(xTranslationRem < 0){
				polygon1Matrix = MeraTransformer.translate(-1,0,polygon1Matrix);
				xTranslationRem++;
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				validate();
				repaint();
			}
			else if(yTranslationRem < 0){
				polygon1Matrix = MeraTransformer.translate(0,-1,polygon1Matrix);
				yTranslationRem++;
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				validate();
				repaint();
			}
			else if(yTranslationRem > 0){
				polygon1Matrix = MeraTransformer.translate(0,1,polygon1Matrix);
				yTranslationRem--;
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				validate();
				repaint();
			}		
		}

		private void rotateShapes() {
			if(angleRem > 0){
				polygon1Matrix = MeraTransformer.rotate(1, rotatePoint.x,rotatePoint.y, polygon1Matrix);
				angleRem--;
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				validate();
				repaint();
			}
			else if(angleRem < 0){
				polygon1Matrix = MeraTransformer.rotate(-1, rotatePoint.x,rotatePoint.y, polygon1Matrix);
				angleRem++;
				poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				validate();
				repaint();
			}
			
		}
		
		private void scaleShape(){
        	if (scaleShape==true){
        		
        		polygon1Matrix = MeraTransformer.scale(scalePointX, scalePointY, scaleFactorX, scaleFactorY, polygon1Matrix);
        		poly1 = new MeraPolygon(polygon1Matrix[0],polygon1Matrix[1],polygon1Matrix[0].length);
				scaleShape = false;
				System.out.print("scaling");
				validate();
				repaintrequired = true;
				repaint();
        	}
        }

		private void drawAxis() {
			origin = new Point();
			origin.y = this.getSize().height/2;
			origin.x = this.getSize().width/2;
			g2d.setPaint(Color.BLACK);
			g2d.drawLine(0,origin.y, this.getBounds().width,origin.y);
			g2d.drawLine(origin.x,0, origin.x,this.getBounds().height);
			Shape originpoint = new Ellipse2D.Double(origin.x-5,origin.y-5,10,10);
			
			float dash1[] = {2.0f};
			g2d.setStroke(new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f));
			for(int i = origin.x + 50; i < origin.x*2; i+=50){
				g2d.drawString(Integer.toString((i - origin.x)/xScale), i - 7, origin.y + 15);
				g2d.drawLine(i,0, i,this.getSize().height);
			}
			
			for(int i = origin.x; i > 0; i-=50){
				g2d.drawString(Integer.toString((i - origin.x)/xScale), i - 10, origin.y + 15);
				g2d.drawLine(i,0, i,this.getSize().height);
			}
			
			for(int i = origin.y - 50; i > 0; i-=50){
				g2d.drawString(Integer.toString((origin.y - i)/yScale), origin.x - 17, i+15);
				g2d.drawLine(0,i, this.getSize().width,i);
			}
			
			for(int i = origin.y + 50; i < origin.y * 2 ; i+=50){
				g2d.drawString(Integer.toString((-(i-origin.y))/yScale), origin.x-20, i-2);
				g2d.drawLine(0,i, this.getSize().width,i);
			}
			g2d.setStroke(new BasicStroke());
		}
	}
	
	public class MeraPoint{
		double x,y;
		public MeraPoint(int x,int y) {
			this.x =  shiftXRev(x);
			this.y =  shiftYRev(y);
		}
		
		public MeraPoint(Point p){
			this(p.x, p.y);
		}

	}
	
	public class MeraPolygon extends Polygon{
		
		public MeraPolygon(double[] polygon,double[] polygon2,int n) {
			super();
			int[] x1 = new int[n];
			int[] y1 = new int[n];
			
			for(int i = 0;i<n;i++){
				x1[i] = (int) shiftX(polygon[i]);
				y1[i] = (int) shiftY(polygon2[i]);
			}
			
			super.npoints = n;
			super.xpoints = x1;
			super.ypoints = y1;
		}
	}
	
	public double shiftX(double polygon){
		double x1 = origin.x + polygon*xScale;
		if((x1 > origin.x*2 || x1 < 0) && posScaleY > 0){
			scaleDown = true;
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
			System.out.printf("Trying to scale down x1 : %f  \n",x1);
			System.out.print("Going to repaint\n");
			xScale = scales[--posScaleX];
			if(scaleBoth)
				yScale = scales[--posScaleY];
			x1 = origin.x + polygon*xScale;
			//repaint();
		}
		return x1; 
	}
	
	public double shiftY(double polygon2){
		double y1 = origin.y - polygon2*yScale;
		if((y1 < 0 || y1> origin.y * 2 ) && posScaleY > 0){
			scaleDown = true;
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
			System.out.printf("Trying to scale down y1 : %f \n",y1);
			System.out.print("Going to repaint\n");
			yScale = scales[--posScaleY];
			if(scaleBoth)
				xScale = scales[--posScaleX];
			y1 = origin.y - polygon2*yScale;
			repaint();
		}
		return y1;
	}
	
	public double shiftXRev(int x1){
		double x = (double)(x1 - origin.x)/(double)xScale;
		return x;
	}
	
	public double shiftYRev(int y1){
		double y = (double)(origin.y - y1)/(double)yScale;
		return y;
	}
}
