import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;

public class EdgeDetection {
    private static BufferedImage grey;
    private static int[][] sobelX;
    private static int[][] sobelY;
    private static double[][] sobelTotal;
    private static String path = System.getProperty("user.dir");
	  private static int maxX = 0;
	  private static int maxY = 0;
	  private static int maxR = 0;

    public static void main(String[] args) throws Exception{
        File originalFile = new File(args[0]);
        toGrayScale(originalFile);
        edgeDetection();
        

        BufferedImage img = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                img.setRGB(i, j, sobelX[i][j]);
            }
        }
        File output = new File(path+"\\result\\sobelX.png");
        ImageIO.write(img, "png", output);


        BufferedImage ing = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                ing.setRGB(i, j, sobelY[i][j]);
            }
        }
        File out= new File(path+"\\result\\sobelY.png");
        ImageIO.write(ing, "png", out);

        BufferedImage total = new BufferedImage(grey.getWidth(), grey.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        double max = 0;
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                if(sobelTotal[i][j]>max){
                    max = sobelTotal[i][j];
                }
            }
        }
        for(int i = 0; i< grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                int rgb = new Color((int)map(sobelTotal[i][j], 0,max,0,255),
                        (int)map(sobelTotal[i][j], 0,max,0,255),
                        (int)map(sobelTotal[i][j], 0,max,0,255), 255).getRGB();
                total.setRGB(i,j,rgb);
            }
        }
        total = changeBrightness(20.0f, total);
        File out2 = new File(path+"\\result\\sobelTotal.png");
        ImageIO.write(total, "png", out2);
    }

    private static double map(double valueCoord1,
                             double startCoord1, double endCoord1,
                             double startCoord2, double endCoord2) {


        double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + startCoord2;
    }

    private static void toGrayScale(File f) throws Exception{
        BufferedImage img = ImageIO.read(f);
        grey = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_BYTE_GRAY);
        grey.getGraphics().drawImage(img, 0 , 0, null);
    }

    private static void edgeDetection(){
        calcSobelX();
        calcSobelY();
        combineSobel();
    }

    private static void calcSobelX(){
        sobelX = new int[grey.getWidth()][grey.getHeight()];
        int[][] base = new int[3][3];
        base[0][0] = -1;
        base[1][0] = -2;
        base[2][0] = -1;
        base[0][1] = 0;
        base[1][1] = 0;
        base[2][1] = 0;
        base[0][2] = 1;
        base[1][2] = 2;
        base[2][2] = 1;


        for(int i = 0; i<grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight();j++){
                sobelX[i][j] = getSobelResult(i,j,base);
            }
        }
    }

    private static void calcSobelY(){
        sobelY = new int[grey.getWidth()][grey.getHeight()];
        int[][] base = new int[3][3];
        base[0][0] = -1;
        base[0][1] = -2;
        base[0][2] = -1;
        base[1][0] = 0;
        base[1][1] = 0;
        base[1][2] = 0;
        base[2][0] = 1;
        base[2][1] = 2;
        base[2][2] = 1;


        for(int i = 0; i<grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight();j++){
                sobelY[i][j] = getSobelResult(i,j,base);
            }
        }
    }

    private static int getSobelResult(int x, int y, int[][] base){
        int result = 0;
        if(x==0 && y ==0){
            for(int i = 0; i <= 1; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==0 && y==grey.getHeight()-1){
            for(int i = 0; i <= 1; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==0 && y !=0){
            for (int i = 0; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    result += new Color(grey.getRGB(x + i, y + j)).getRed()* base[j + 1][i + 1];
                }
            }

        }else if(x==grey.getWidth()-1 && y ==0){
            for(int i = -1; i <= 0; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x!=0 && y ==0){
            for(int i = -1; i <= 1; i++){
                for(int j = 0; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==grey.getWidth()-1 && y==grey.getHeight()-1){
            for(int i = -1; i <= 0; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x==grey.getWidth()-1 && y!=grey.getHeight()-1){
            for(int i = -1; i <= 0; i++){
                for(int j = -1; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else if(x!=grey.getWidth()-1 && y==grey.getHeight()-1){
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 0; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }else{
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    result += new Color(grey.getRGB(x+i,y+j)).getRed()*base[j+1][i+1];
                }
            }
        }

        return result;
    }

    private static void combineSobel(){
        sobelTotal = new double[grey.getWidth()][grey.getHeight()];
        for(int i = 0; i <grey.getWidth(); i++){
            for(int j = 0; j<grey.getHeight(); j++){
                sobelTotal[i][j] = Math.round(Math.sqrt(Math.pow((double)sobelX[i][j],2) + Math.pow((double)sobelY[i][j],2)));
            }
        }
    }

    private static BufferedImage changeBrightness(float brightenFactor, BufferedImage image){
        RescaleOp op = new RescaleOp(brightenFactor, 0, null);
        image = op.filter(image, image);
        return image;
    }
}
