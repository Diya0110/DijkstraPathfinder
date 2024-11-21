import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GridShortestPathFinder extends JFrame {
    private final int GRID_SIZE = 10; // Grid size (10x10)
    private final JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private Point startPoint = null;
    private Point endPoint = null;
    private final Set<Point> blockedNodes = new HashSet<>();

    public GridShortestPathFinder() {
        setTitle("Grid Shortest Path Finder");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        initializeGrid(gridPanel);

        JPanel controlPanel = new JPanel();
        JButton findPathButton = new JButton("Find Shortest Path");
        findPathButton.addActionListener(e -> findShortestPath());
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGrid());
        controlPanel.add(findPathButton);
        controlPanel.add(resetButton);

        add(gridPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void initializeGrid(JPanel gridPanel) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JButton button = new JButton();
                button.setBackground(Color.BLACK);
                button.setPreferredSize(new Dimension(50, 50));
                button.setFocusPainted(false);

                final int x = i;
                final int y = j;

                button.addActionListener(e -> handleGridClick(button, x, y));
                gridButtons[i][j] = button;
                gridPanel.add(button);
            }
        }
    }

    private void handleGridClick(JButton button, int x, int y) {
        Point clickedPoint = new Point(x, y);

        if (startPoint == null) {
            startPoint = clickedPoint;
            button.setBackground(Color.BLUE);
        } else if (endPoint == null && !clickedPoint.equals(startPoint)) {
            endPoint = clickedPoint;
            button.setBackground(Color.RED);
        } else if (!clickedPoint.equals(startPoint) && !clickedPoint.equals(endPoint)) {
            if (blockedNodes.contains(clickedPoint)) {
                blockedNodes.remove(clickedPoint);
                button.setBackground(Color.BLACK);
            } else {
                blockedNodes.add(clickedPoint);
                button.setBackground(Color.WHITE); 
            }
        }
    }

    private void findShortestPath() {
        if (startPoint == null || endPoint == null) {
            JOptionPane.showMessageDialog(this, "Please set both Start and End points.");
            return;
        }

        java.util.List<Point> path = dijkstra();

        if (path == null) {
            JOptionPane.showMessageDialog(this, "No path found!");
        } else {
            for (Point p : path) {
                if (!p.equals(startPoint) && !p.equals(endPoint)) {
                    gridButtons[p.x][p.y].setBackground(Color.BLUE); 
                }
            }
            JOptionPane.showMessageDialog(this, "Path found! Size: " + (path.size() - 1) + " steps.");
        }
    }

    private java.util.List<Point> dijkstra() {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        Map<Point, Integer> distances = new HashMap<>();
        Map<Point, Point> previous = new HashMap<>();

        // Initialize distances
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Point point = new Point(i, j);
                distances.put(point, Integer.MAX_VALUE);
            }
        }
        distances.put(startPoint, 0);
        queue.add(new Node(startPoint, 0));

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            Point currentPoint = currentNode.point;

            if (currentPoint.equals(endPoint)) {

                return reconstructPath(previous);
            }

            for (Point neighbor : getNeighbors(currentPoint)) {
                if (blockedNodes.contains(neighbor))
                    continue;

                int newDist = distances.get(currentPoint) + 1;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentPoint);
                    queue.add(new Node(neighbor, newDist));
                }
            }
        }
        return null;
    }

    private java.util.List<Point> reconstructPath(Map<Point, Point> previous) {
        java.util.List<Point> path = new ArrayList<>();
        Point current = endPoint;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        return path;
    }

    private java.util.List<Point> getNeighbors(Point point) {
        int x = point.x;
        int y = point.y;

        java.util.List<Point> neighbors = new ArrayList<>();
        if (x > 0)
            neighbors.add(new Point(x - 1, y));
        if (x < GRID_SIZE - 1)
            neighbors.add(new Point(x + 1, y));
        if (y > 0)
            neighbors.add(new Point(x, y - 1));
        if (y < GRID_SIZE - 1)
            neighbors.add(new Point(x, y + 1));

        return neighbors;
    }

    private void resetGrid() {
        startPoint = null;
        endPoint = null;
        blockedNodes.clear();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JButton button = gridButtons[i][j];
                button.setBackground(Color.BLACK); // Reset background to black
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GridShortestPathFinder frame = new GridShortestPathFinder();
            frame.setVisible(true);
        });
    }

    private static class Node {
        Point point;
        int distance;

        Node(Point point, int distance) {
            this.point = point;
            this.distance = distance;
        }
    }
}
