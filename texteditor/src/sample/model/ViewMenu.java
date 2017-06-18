package sample.model;

import sample.controller.Controller;

public class ViewMenu {
    Controller controller;
    Model model;
    public ViewMenu(Controller controller, Model model){
        this.controller = controller;
        this.model = model;
    }
}
