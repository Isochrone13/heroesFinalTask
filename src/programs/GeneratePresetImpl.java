package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int sameTypeLimit = 11;
    private static final int fieldWidth = 3;
    private static final int fieldHeight = 21;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
         /*
        Сортируем список unitList по убыванию эффективности используя метод sort,
        которому передается лямбда-выражение (принимает два объекта (a и b) и реализует логику сравнения).
        Эффективность каждого юнита рассчитывается как (атака + здоровье) / стоимость.
        */
        unitList.sort((a, b) -> {
            double efficiencyA = (double) (a.getBaseAttack() + a.getHealth()) / a.getCost();
            double efficiencyB = (double) (b.getBaseAttack() + b.getHealth()) / b.getCost();
            return Double.compare(efficiencyB, efficiencyA);
        });

        /*
        Создаём словарь, где ключом является тип юнита,
        а значением — текущий счётчик добавленных юнитов этого типа.
        */
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (Unit unit : unitList) {
            typeCountMap.put(unit.getUnitType(), 0);
        }

        // Создаём список уже созданных юнитов, которые попадают в финальную армию компьютера.
        List<Unit> selectedUnits = new ArrayList<>();

        int currentPoints = 0;
        Random random = new Random();

        // Множество занятых клеток, чтобы не ставить двух юнитов в одно место
        Set<String> usedPositions = new HashSet<>();

        /*
        Пока в одном полном проходе по списку unitList
        мы можем добавить хотя бы один юнит, идём по циклу.
        */
        boolean unitAdded;
        do {
            unitAdded = false;
            for (Unit template : unitList) {
                int cost = template.getCost();
                int currentTypeCount = typeCountMap.get(template.getUnitType());

                // Проверяем лимит в 11 штук для типа и доступные очки
                if (currentTypeCount < sameTypeLimit && (currentPoints + cost) <= maxPoints) {
                    while (true) {
                        int x = random.nextInt(fieldWidth);
                        int y = random.nextInt(fieldHeight);
                        String posKey = x + "," + y;

                        // Проверяем, не занята ли клетка
                        if (!usedPositions.contains(posKey)) {
                            usedPositions.add(posKey); // Занимаем клетку

                            // Создаём клон юнита (уникальное имя + координаты)
                            Unit newUnit = new Unit(
                                    template.getName() + " " + (currentTypeCount + 1),
                                    template.getUnitType(),
                                    template.getHealth(),
                                    template.getBaseAttack(),
                                    template.getCost(),
                                    template.getAttackType(),
                                    template.getAttackBonuses(),
                                    template.getDefenceBonuses(),
                                    x,
                                    y
                            );
                            selectedUnits.add(newUnit);

                            // Обновляем счетчики и флаг
                            typeCountMap.put(template.getUnitType(), currentTypeCount + 1);
                            currentPoints += cost;
                            unitAdded = true;
                            break; // Выходим из бесконечного цикла
                        }
                    }
                }
            }
        } while (unitAdded);

        // Создаём и возвращаем армию
        Army army = new Army(selectedUnits);
        army.setPoints(currentPoints);
        return army;
    }
}