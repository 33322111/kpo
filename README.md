**Ресторанная система**

Данная программа представляет собой консольное приложение для управления ресторанным меню, оформления заказов и автоматизации процесса приготовления блюд. Проект реализован на языке программирования Kotlin.

**Используемые шаблоны:**

**Шаблон "Команда":** 
Команды для управления меню и заказами обрабатываются в виде отдельных объектов, что позволяет добавлять новые команды без изменения основного кода.

**Шаблон "Стратегия":** 
Реализация различных стратегий обработки заказов, например, многопоточная обработка.

**Шаблон "Наблюдатель":** 
Система оповещает посетителей о состоянии и статусе своих заказов.

Программа предоставляет два типа пользователей: посетителей и администраторов. Они могут регистрироваться и заходить в приложение. Пока пользователь не аутентифицирован, ему не будет предложено основное меню выбора действий. 

**Меню администратора:**
1. Добавить блюдо
2. Удалить блюдо
3. Редактировать блюдо
4. Посмотреть всё меню
5. Посмотреть доход ресторана
6. Закончить смену

**Меню посетителя:**
1. Посмотреть всё меню
2. Сделать заказ
3. Добавить блюдо в заказ
4. Удалить заказ
5. Оплатить заказы
6. Выйти из ресторана

Данные обо всех пользователях, заказах и меню сохраняются в отдельных файлах, чтобы сделать приложение более гибким и универсальным.
