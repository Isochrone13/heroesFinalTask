package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    // Размеры игрового поля
    private static final int fieldWidth = 27;
    private static final int fieldHeight = 21;

    // Возможные направления движения: влево, вправо, вверх, вниз
    private static final int[][] direction = {
            {-1, 0}, // Влево
            {1, 0},  // Вправо
            {0, -1}, // Вверх
            {0, 1}   // Вниз
    };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Начальная и целевая точки на игровом поле
        Edge start = new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        Edge goal = new Edge(targetUnit.getxCoordinate(), targetUnit.getyCoordinate());

        // Создаём массив занятых клеток. Если клетка занята другим юнитом, она становится недоступной.
        boolean[][] occupied = new boolean[fieldWidth][fieldHeight];
        for (Unit unit : existingUnitList) {
            if (unit.isAlive()) {
                int ux = unit.getxCoordinate();
                int uy = unit.getyCoordinate();
                // Целевую клетку можно занимать атакующим, чтобы завершить путь
                if (!(ux == goal.getX() && uy == goal.getY())) {
                    occupied[ux][uy] = true;
                }
            }
        }

        /*
        Храним, из какой клетки мы пришли в текущую
        Позволяет восстановить путь после завершения поиска
        Если originalSquare[x][y] = null, значит клетка (x, y) не посещалась.
         */
        Edge[][] originalSquare = new Edge[fieldWidth][fieldHeight];

        // Очередь для поиска пути
        Queue<Edge> queue = new LinkedList<>();
        queue.offer(start); // Добавляем начальную клетку в очередь

        // Помечаем начальную клетку как посещённую (её "родитель" — сама она)
        originalSquare[start.getX()][start.getY()] = start;

        /*
        Запускаем поиск кратчайшего пути
        Обходим клетки слоями (в порядке увеличения расстояния от начальной клетки).
         */
        while (!queue.isEmpty()) {
            // Берём текущую клетку из очереди
            Edge current = queue.poll();
            int cx = current.getX();
            int cy = current.getY();

            // Проверяем все соседние клетки (по всем направлениям)
            for (int[] d : direction) {
                int nx = cx + d[0]; // Новая координата X
                int ny = cy + d[1]; // Новая координата Y

                // Если соседняя клетка доступна, не занята и ещё не посещена
                if (isValid(nx, ny) && !occupied[nx][ny] && originalSquare[nx][ny] == null) {
                    originalSquare[nx][ny] = current; // Помечаем текущую клетку как исходную для соседней
                    queue.offer(new Edge(nx, ny)); // Добавляем соседнюю клетку в очередь

                    // Если достигли цели, восстанавливаем путь и возвращаем его
                    if (nx == goal.getX() && ny == goal.getY()) {
                        return buildPath(originalSquare, start, goal);
                    }
                }
            }
        }

        // Если очередь закончилась, но цель не достигнута — путь отсутствует
        return Collections.emptyList();
    }

    /*
    Проверяем, находится ли клетка (x, y) в пределах игрового поля.
    */
    private boolean isValid(int x, int y) {
        return x >= 0 && x < fieldWidth && y >= 0 && y < fieldHeight;
    }

    /*
    Восстанавливаем путь от начальной клетки к цели, используя массив originalSquare.
    */
    private List<Edge> buildPath(Edge[][] parent, Edge start, Edge goal) {
        List<Edge> path = new ArrayList<>();
        Edge cur = goal; // Начинаем восстановление пути с цели
        while (!cur.equals(start)) { // Пока не достигнем начальной клетки
            path.add(cur); // Добавляем текущую клетку в путь
            cur = parent[cur.getX()][cur.getY()]; // Переходим к исходной клетке
        }
        path.add(start); // Добавляем начальную клетку в конец пути
        Collections.reverse(path); // Разворачиваем путь, чтобы он шёл от start к goal
        return path;
    }
}