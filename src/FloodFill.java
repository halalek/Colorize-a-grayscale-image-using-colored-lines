import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {
    private static boolean validCoordinate(int x, int y, int n, int m) {
        return x >= 0 && y >= 0 && x < n && y < m;
    }

    public static void run(
            int width,
            int height,
            int pixelX,
            int pixelY,
            int[][] redChannel,
            int[][] greenChannel,
            int[][] blueChannel,
            int[][] redMask,
            int[][] greenMask,
            int[][] blueMask,
            int newRed,
            int newGreen,
            int newBlue
    ) {
        boolean[][] visited = new boolean[height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                visited[y][x] = false;
            }
        }

        Queue<Pair> obj = new LinkedList<>();

        Pair pq = new Pair(pixelX, pixelY);
        obj.add(pq);

        visited[pixelY][pixelX] = true;

        while (!obj.isEmpty()) {
            Pair coordinate = obj.peek();
            int x = coordinate.first;
            int y = coordinate.second;
            int preColorRed = redChannel[y][x];
            int preColorGreen = greenChannel[y][x];
            int preColorBlue = blueChannel[y][x];
            
            redMask[y][x] = newRed;
            greenMask[y][x] = newGreen;
            blueMask[y][x] = newBlue;

            obj.remove();

            if (
                    validCoordinate(x, y - 1, width, height) &&
                    !visited[y - 1][x] &&
                    redChannel[y - 1][x] == preColorRed &&
                    greenChannel[y - 1][x] == preColorGreen &&
                    blueChannel[y - 1][x] == preColorBlue
            ) {
                Pair p = new Pair(x, y - 1);
                obj.add(p);
                visited[y - 1][x] = true;
            }

            if (
                    validCoordinate(x, y + 1, width, height) &&
                    !visited[y + 1][x] &&
                    redChannel[y + 1][x] == preColorRed &&
                    greenChannel[y + 1][x] == preColorGreen &&
                    blueChannel[y + 1][x] == preColorBlue
            ) {
                Pair p = new Pair(x, y + 1);
                obj.add(p);
                visited[y + 1][x] = true;
            }

            if (
                    validCoordinate(x + 1, y, width, height) &&
                    !visited[y][x + 1] &&
                    redChannel[y][x + 1] == preColorRed &&
                    greenChannel[y][x + 1] == preColorGreen &&
                    blueChannel[y][x + 1] == preColorBlue
            ) {
                Pair p = new Pair(x + 1, y);
                obj.add(p);
                visited[y][x + 1] = true;
            }

            if (
                    validCoordinate(x - 1, y, width, height) &&
                    !visited[y][x - 1] &&
                    redChannel[y][x - 1] == preColorRed &&
                    greenChannel[y][x - 1] == preColorGreen &&
                    blueChannel[y][x - 1] == preColorBlue
            ) {
                Pair p = new Pair(x - 1, y);
                obj.add(p);
                visited[y][x - 1] = true;
            }
        }
    }

}
