package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;


import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClassesSetsTest {
    public Employee a1;
    public Employee a2;
    public Employee a3;
    public Employee b1;
    public Employee b2;
    public Employee c1;
    public Employee c2;

    public Project p1;
    public Project p2;
    public Project p3;

    public Set<Employee> employees;
    public Set<Project> projects;

    @BeforeEach
    public void beforeEach() {
        a1 = new Employee("Ivan");
        a2 = new Employee("Ivan");
        a3 = new Employee("Ivan");

        b1 = new Employee("Justyna");
        b2 = new Employee("Justyna");

        c1 = new Employee("Carl");
        c2 = new Employee("Carl");


        p1 = new Project("Java");
        p2 = new Project("Python");
        p3 = new Project("Java");

        employees = new HashSet<>();
        projects = new HashSet<>();

        employees.add(a1);
        employees.add(a2);
        employees.add(a3);
        employees.add(b1);
        employees.add(b2);
        employees.add(c1);
        employees.add(c2);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        a1.addProject(p1);
        a2.addProject(p2);
        a3.addProject(p3);

        p1.addEmployee(a1);
        p2.addEmployee(a2);
        p3.addEmployee(a3);

        p1.addEmployee(b1);
        p1.addEmployee(c2);
        p1.addEmployee(c1);


    }


    @Test
    public void testIfEmployeesDontDuplicate() {
        assertEquals(3, employees.size());
    }

    @Test
    public void testIfNewObjectWithSameNameAlreadyInSet() {
        Employee test = new Employee("Ivan");
        assertTrue(employees.contains(test));
    }

//    @Test
//    public void testIfSetsDoNotHaveDuplicates() {
////        for (Project p : a1.getProjects()) {
////            System.out.println(p.getName());
////        }
//        assertEquals(2, a1.getProjects().size());
//    }
}

