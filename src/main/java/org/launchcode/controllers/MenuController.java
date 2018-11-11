package org.launchcode.controllers;



import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    CheeseDao cheeseDao;

    @Autowired
    MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("title", "Menu");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute(new Menu());
        model.addAttribute("title", "Menu");

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Menu");
            return "menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        model.addAttribute("menu", menu);
        model.addAttribute("id", menu.getId());

        return "menu/view";
    }

    @RequestMapping(value="add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form", form);
        model.addAttribute("title", "Add Item to Menu: " + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value="add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){
        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        }
        Cheese cheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(cheese);
        menuDao.save(theMenu);
        return "redirect:view/" + theMenu.getId();
    }
}
