package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmy) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Проходим по каждой строке
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }

            // Если атакует левая армия (компьютер), ищем правого юнита (то есть идём справа налево)
            if (isLeftArmy) {
                for (int i = row.size() - 1; i >= 0; i--) {
                    Unit unit = row.get(i);
                    if (unit != null && unit.isAlive()) {
                        // Как только нашли живого - это юнит, который не заблокирован
                        suitableUnits.add(unit);
                        break;
                    }
                }
            }
            // Иначе атакует правая армия (игрок), ищем слева направо
            else {
                for (int i = 0; i < row.size(); i++) {
                    Unit unit = row.get(i);
                    if (unit != null && unit.isAlive()) {
                        // Как только нашли живого - это юнит, который не заблокирован
                        suitableUnits.add(unit);
                        break;
                    }
                }
            }
        }

        return suitableUnits;
    }
}