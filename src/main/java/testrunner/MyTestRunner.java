package testrunner;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyTestRunner {
    public void runAllTests() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Package classPackage = MyTestRunner.class.getPackage();

        List<Class<?>> allClassesFrom = getAllClassesFrom(classPackage.getName());
        for (Class nextClass : allClassesFrom) {

            //System.out.println(nextClass.getName());
//            if (nextClass.getName().equals("testrunner.MyTestCase")) {
//                continue;
//            }
            Constructor constructor = nextClass.getDeclaredConstructor();

            constructor.setAccessible(true);
            Object obj = constructor.newInstance();
            Method[] methods = nextClass.getMethods();
            for (Method nextMethod : methods) {
                if (nextMethod.isAnnotationPresent(MyTestCase.class)) {
                    //System.out.println(nextMethod.getName());
                    nextMethod.setAccessible(true);
                    Object invoked = nextMethod.invoke(obj);
                }
            }
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new MyTestRunner().runAllTests();
    }

    private static List<Class<?>> getAllClassesFrom(String packageName) {
        return new Reflections(packageName, new SubTypesScanner(false))
                .getAllTypes()
                .stream()
                .map(name -> {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
