package com.train.springboot.springboot.controller;

import com.train.springboot.springboot.dao.DepartmentDao;
import com.train.springboot.springboot.dao.EmployeeDao;
import com.train.springboot.springboot.entities.Department;
import com.train.springboot.springboot.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

/**
 * @author yidong
 * @create 2019-02-13-09:09
 */
@Controller
public class EmployeeController {
    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private DepartmentDao departmentDao;

    @GetMapping("/emps")
    public String list(Model model){
        Collection<Employee> emps = employeeDao.getAll();
        model.addAttribute("emps",emps);

        return "emp/list";
    }

    @GetMapping("/emp")
    public String toAdd(Model model){
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts",departments);
        return "emp/add";
    }
}
