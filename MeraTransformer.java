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

public class MeraTransformer {
	static final int[][] reflectOnXMatrix = {{1,0,0},{0,-1,0},{0,0,1}};
	static final int[][] reflectonYMatrix = {{-1,0,0},{0,1,0},{0,0,1}};
	static final int[][] reflectonXYMatrix = {{-1,0,0},{0,-1,0},{0,0,1}};
	static final int[][] reflectonYisXMatrix = {{0,1,0},{1,0,0},{0,0,1}};
	static final int[][] reflectonyisiXMatrix = {{0,-1,0},{-1,0,0},{0,0,1}};
	
	public static double[][] reflectOnXAxis(double[][] polygon){
		return multiplyMatrix(reflectOnXMatrix, polygon);
	}
	
	public static double[][] reflectOnYAxis (double [][] polygon){
		return multiplyMatrix (reflectonYMatrix, polygon);
	}
	
	public static double[][] translate(int x,int y,double[][] matrix){
		int[][] translateMatrix = {{1,0,x},{0,1,y},{0,0,1}};
		return multiplyMatrix(translateMatrix, matrix);
	}
	
	public static double[][] rotate(double angle,int x,int y,double[][] matrix){
		return translate(x,y,rotate(angle,translate(-x,-y,matrix)));
	}
	
	public static double[][] rotate(double angle,double[][] matrix){
		angle = Math.PI * angle / 180;
		double[][] rotateMatrix = {{Math.cos(angle),-Math.sin(angle),0},{Math.sin(angle),Math.cos(angle),0},{0,0,1}};
		return multiplyMatrix(rotateMatrix, matrix);
	}
	
	public static double[][] multiplyMatrix(int[][] m1,double[][] polygon){
		int noRows = polygon.length;
		int noCols = polygon[0].length;
		double[][] result = new double[noRows][noCols];
		for(int i = 0;i<noRows;i++){
			for(int j = 0;j<noCols;j++){
				result[i][j] = 0;
				for(int k = 0;k<3;k++){
					result[i][j] = result[i][j] + m1[i][k] * polygon[k][j];
				}
			}
		}
		return result;
	}
	
	public static double[][] multiplyMatrix(double[][] m1,double[][] m2){
		int noRows = m2.length;
		int noCols = m2[0].length;
		double[][] result = new double[noRows][noCols];
		for(int i = 0;i<noRows;i++){
			for(int j = 0;j<noCols;j++){
				result[i][j] = 0;
				for(int k = 0;k<3;k++){
					result[i][j] = (result[i][j] + m1[i][k] * m2[k][j]);
				}
			}
		}
		
		return result;
	}
	
	public static double [][] scale (double sx, double sy, double[][] matrix){
		double[][] scaleMatrix={{sx,0,0},{0,sy,0},{0,0,1}};
		return multiplyMatrix(scaleMatrix,matrix);
	}
	
	public static double[][] scale(int x , int y, double sx, double sy, double[][] matrix){
		return translate (x,y,(scale(sx,sy,(translate(-x,-y,matrix)))));
	
	}
	
	public static double[][] shearX (double sx, double yref, double[][] matrix){
		double[][] shearXMatrix={{1,sx,-yref},{0,1,0},{0,0,1}};
		return multiplyMatrix(shearXMatrix, matrix);
	}
	public static double[][] shearY(double sy,double xref, double[][]matrix){
		double[][] shearYMatrix={{1,0,0},{sy,1,-xref},{0,0,1}};
		return multiplyMatrix (shearYMatrix, matrix);
	}
}
