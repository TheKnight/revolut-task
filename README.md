## Тестовое задание Revolut

Для сборки запустите в директории с проектом команду

```
./gradew shadowJar
```
Итоговый jar-файл с включенными в него зависимостями будет распологаться по пути
```
${project.dir}/build/libs/revolut-task-1.0-SNAPSHOT-fat.jar
```

Для запуска можно воспользоваться командой

```
java -jar ${project.dir}/build/libs/revolut-task-1.0-SNAPSHOT-fat.jar
```

В рамках данного тестового задания были реализованы следующие методы:

1. Create account - создание нового счета с некой начальной суммой.
```
curl -X PUT localhost:8080/create?account=1\&amount=5
```

2. Account status - текущее состояние счета.
```
curl -X GET localhost:8080/status/1
```

3. Transfer - перемещение денег со счета на счет.
```
curl -X POST localhost:8080/transfer?from=2\&to=1\&amount=20
```

Нюансы:
1. Не умеет работать с номерами аккаунтов не укладывающимися в Integer.MAX_VALUE.
2. Аккаунт не может иметь отрицательный баланс - операция, результатом которой станет уменьшение баланса одного из аккаунтов ниже нуля, будет отвергнута.
3. Не различаются случаи не существования аккаунта источника и аккаунта приемника.

