package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog; // Логирование после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Получаем изменяемые списки живых юнитов
        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        // Пока у обеих армий есть живые юниты — идёт бой
        while (hasAlive(playerUnits) && hasAlive(computerUnits)) {
            // Сортируем по убыванию базовой атаки (в начале каждого раунда)
            sortDescByAttack(playerUnits);
            sortDescByAttack(computerUnits);

            // Формируем "чередующийся" список (очередь) для атаки
            List<Unit> attackOrder = unitMoves(playerUnits, computerUnits);

            // Проходим по attackOrder
            for (Unit attacker : attackOrder) {
                // Проверяем, не умер ли юнит
                if (!attacker.isAlive()) {
                    continue;
                }
                // Выполняем атаку
                Unit target = attacker.getProgram().attack();
                if (target != null) {
                    // Выводим лог
                    printBattleLog.printBattleLog(attacker, target);
                }
            }

            // Убираем мёртвых юнитов из списков
            removeDead(playerUnits);
            removeDead(computerUnits);
        }
    }

    // Проверяем, есть ли хоть один живой юнит
    private boolean hasAlive(List<Unit> units) {
        return units.stream().anyMatch(Unit::isAlive);
    }

    // Сортировка по убыванию базовой атаки
    private void sortDescByAttack(List<Unit> units) {
        units.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
    }

    // Чередуем: юнит игрока, юнит компьютера, и т.д.
    private List<Unit> unitMoves(List<Unit> playerUnits, List<Unit> computerUnits) {
        List<Unit> result = new ArrayList<>();
        int size = Math.max(playerUnits.size(), computerUnits.size());
        for (int i = 0; i < size; i++) {
            if (i < playerUnits.size()) {
                result.add(playerUnits.get(i));
            }
            if (i < computerUnits.size()) {
                result.add(computerUnits.get(i));
            }
        }
        return result;
    }

    // Удаляем мёртвых
    private void removeDead(List<Unit> units) {
        units.removeIf(u -> !u.isAlive());
    }
}