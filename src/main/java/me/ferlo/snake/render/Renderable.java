/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferlo.snake.render;

import java.awt.Graphics;

/**
 *
 * @author ferlin_francesco
 */
public interface Renderable {
    int HIGH_PRIORITY = 1;
    int NORMAL_PRIORITY = 0;
    int LOW_PRIORITY = -1;
    
    void paint(Graphics g);
    int getPriority();
}
