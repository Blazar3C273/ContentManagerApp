import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import org.junit.runners.Suite;

/**
 * Created by Tolik on 25.11.2014.
 */
@RunWith(Categories.class)
@Category(AllTests.class)
@Categories.IncludeCategory(MyTest.class)
@Suite.SuiteClasses({MyTest.class, AllTestsRunner.class, HelloWorldTest.class, PropertiesLoaderTest.class, dbConnectTest.class, SerializationDeserializationItemsTest.class})
public class AllTestsRunner {
    //TODO Обработчики
    /*
    * Пункт меню добавить экспонат+
    * Пункт меню Сохраить экспонаты+
    * Пункт меню Распечатать VIP коды
    * Пункт меню Выход
    * Пункт меню управление категориями
    * Пункт меню управление обратной связью+
    *Пункт меню о программе
    * Изменилось название Item+
    * Изменилось описание+
    * Изменилась категори
    * Измененеия в модели AttachmentTable+
    * Кнопка Добавить файл+
    * Кнопка Удалить файл+
    * */
    //QRcodeImage redraw

    //TODO Аккаунты и управление доступом

    // Attachment features
    //Загрузка с сервера - add method in deserializer
    //Загрузка на сервер - add new method in NetworkConnection class
    //Диалог выбора файла
    //Операция удаления файла

    //Showing Item on GUI
    //Filling Attachments Table from Current Item.

    //Editing Item
    //Editing attachment table

    //Loading Changed Item on Server - куда запихнуть эту функцию? В кнопку? В меню? Сохранять автоматом? При выходе из программы?

    //Resize problem - ОК

    //TODO Add Deserializer on Mobile App

    //TODO FeedBack feature
    //DB side: New DB "feedback",New format, new
    //TODO Генерация VIP QR cods.
    //TODO DB side
    //Добавить новую Базу
    //Добавить новый индекс
    //TODO ManagerApp
    //Добавить десериализатор
    //Добавить метод генерации
    //Добавить функцию записи кодов в файл
    //TODO MobileApp
    //Добавить настройки сервера
    //Обработать ситуации с ошибками сети
    //Добавить запрос и логику ограничения доступа
    //Создание нового Экспоната
    //добавить обработчик к кнопке
    //TODO Добавить в мокап MobileAPP Feedback Activity

    //TODO Del Item
    //исследовать как удалить записи в бд
    //просто добавить поле или удалять его или добавить логику, которая не выдает при запросе удаленные Item
    //TODO Category Management Dialog
    //Как управлять категориями? Для создания категории нужно создать хотябы один экспонат. Чтобы удалить нужно удалить все экспонаты в данной категории.

}
