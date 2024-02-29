import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Colorize {

    static int [] sobel_y =    {1,	0, 	-1,
            2,	0,	-2,
            1,	0,	-1};

    static int [] sobel_x = 	{1,  2,  1,
            0,	 0,	 0,
            -1,	-2,	-1};

    //Canny parameters
    private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
    private static final int CANNY_STD_DEV = 1;             //Range 1-3
    public BufferedImage outputImg;


    public BufferedImage colorizeImage(BufferedImage inputImg, int pixelX, int pixelY, int r, int g, int b) {

        BufferedImage edgesX = edgeDetection(inputImg, sobel_x);
        BufferedImage edgesY = edgeDetection(inputImg, sobel_y);
        BufferedImage img = sobel(edgesX,edgesY);

        int orgWidth = inputImg.getWidth();
        int orgHeight = inputImg.getHeight();

        int width = img.getWidth();
        int height = img.getHeight();

        int widthDif = orgWidth - width;
        int heightDif = orgHeight - height;

        int y1 = heightDif/2;
        int x1 = widthDif/2;

        outputImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] orgRedChannel = new int[orgHeight][orgWidth];
        int[][] orgGreenChannel = new int[orgHeight][orgWidth];
        int[][] orgBlueChannel = new int[orgHeight][orgWidth];

        int[][] redChannel = new int[height][width];
        int[][] greenChannel = new int[height][width];
        int[][] blueChannel = new int[height][width];

        // three channels of mask
        int[][] redMask = new int[height][width];
        int[][] greenMask = new int[height][width];
        int[][] blueMask = new int[height][width];

        int[][] orgRedMask = new int[orgHeight][orgWidth];
        int[][] orgGreenMask = new int[orgHeight][orgWidth];
        int[][] orgBlueMask = new int[orgHeight][orgWidth];


        for (int y = 0; y < orgHeight; y++) {
            for (int x = 0; x < orgWidth; x++) {
                //Retrieving contents of a pixel
                int pixel = inputImg.getRGB(x, y);

                //Creating a Color object from pixel value
                Color color = new Color(pixel, false);

                //Retrieving the R G B values
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                orgRedChannel[y][x] = red;
                orgGreenChannel[y][x] = green;
                orgBlueChannel[y][x] = blue;
            }
        }

        if (height > 0 && width > 0) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //Retrieving contents of a pixel
                    int pixel = img.getRGB(x, y);

                    //Creating a Color object from pixel value
                    Color color = new Color(pixel, false);

                    //Retrieving the R G B values
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();

                    redChannel[y][x] = red;
                    greenChannel[y][x] = green;
                    blueChannel[y][x] = blue;
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                redMask[y][x] = -1;
                greenMask[y][x] = -1;
                blueMask[y][x] = -1;
            }
        }

        FloodFill.run(width, height, pixelX, pixelY,
                redChannel, greenChannel, blueChannel,
                redMask,greenMask,blueMask, r,g,b);

        for (int y = 0; y < orgHeight; y++) {
            for (int x = 0; x < orgWidth; x++) {
                if( y < y1 || x < x1 || y >= (orgHeight - y1) || x >= (orgWidth - x1) ){

                    orgRedMask[y][x] = -1;
                    orgGreenMask[y][x] = -1;
                    orgBlueMask[y][x] = -1;
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                orgRedMask[y+y1][x+x1] = redMask[y][x];
                orgGreenMask[y+y1][x+x1] = greenMask[y][x];
                orgBlueMask[y+y1][x+x1] = blueMask[y][x];
            }
        }

        for (int y = 0; y < orgHeight; y++) {
            for (int x = 0; x < orgWidth; x++) {
                if(orgRedMask[y][x] != -1 && orgGreenMask[y][x] != -1 && orgBlueMask[y][x] != -1){
                    orgRedChannel[y][x] = orgRedMask[y][x];
                    orgGreenChannel[y][x] = orgGreenMask[y][x];
                    orgBlueChannel[y][x] = orgBlueMask[y][x];
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int R = orgRedChannel[y][x];
                int G = orgGreenChannel[y][x];
                int B = orgBlueChannel[y][x];

                int rgb = R;
                rgb = (rgb << 8) + G;
                rgb = (rgb << 8) + B;
                outputImg.setRGB(x, y, rgb);
            }
        }

        System.out.println("Done");

        return outputImg;
    }


    public void saveImage(String imgPath, BufferedImage outputImg) throws IOException {

        String inputImgPath = imgPath.substring(6);

        String imgExt = inputImgPath.split("\\.")[1];

        String outputImgPath="";
        if(inputImgPath.contains(".png")){
            outputImgPath = inputImgPath.replace(".png", "_colored.png");
        }
        else {
            outputImgPath = inputImgPath.replace(".jpg", "_colored.jpg");
        }


        ImageIO.write(outputImg, imgExt, new File(outputImgPath));
    }

    public BufferedImage sobel (BufferedImage edgesX, BufferedImage edgesY){
        BufferedImage result = new BufferedImage(edgesX.getWidth(), edgesX.getHeight(), BufferedImage.TYPE_INT_RGB);
        int height = result.getHeight();
        int width = result.getWidth();
        for(int x = 0; x < width ; x++){
            for(int y = 0; y < height; y++){
                int tmp = Math.abs(edgesX.getRGB(x, y)&0xff) + Math.abs(edgesY.getRGB(x, y)&0xff);
                result.setRGB(x,y, 0xff000000|(tmp<<16)|(tmp<<8)|tmp);
            }
        }
        return result;
    }

    public BufferedImage edgeDetection(BufferedImage img, int[] kernel){
        int height = img.getHeight();
        int width = img.getWidth();

        BufferedImage result = new BufferedImage(width -1, height -1, BufferedImage.TYPE_INT_RGB);
        for(int x = 1; x < width -1 ; x++){
            for(int y = 1; y < height - 1; y++){
                int [] tmp = {img.getRGB(x-1, y-1)&0xff,img.getRGB(x, y-1)&0xff,img.getRGB(x+1, y-1)&0xff,
                        img.getRGB(x-1, y)&0xff,img.getRGB(x, y)&0xff,img.getRGB(x+1, y)&0xff,img.getRGB(x-1, y+1)&0xff,
                        img.getRGB(x, y+1)&0xff,img.getRGB(x+1, y+1)&0xff};
                int value = convolution (kernel, tmp);
                result.setRGB(x,y, 0xff000000|(value<<16)|(value<<8)|value);
            }
        }
        return result;
    }

    public int convolution (int [] kernel, int [] pixel){
        int result = 0;

        for (int i = 0; i < pixel.length; i++){
            result += kernel[i] * pixel[i];
        }
        return (int)(Math.abs(result) / 9);
    }


    public BufferedImage greyscale(BufferedImage img){

        int width = img.getWidth();
        int height = img.getHeight();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x,y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                int avg = (r+g+b)/3;

                p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                img.setRGB(x, y, p);
            }
        }
        return img;
    }

}
