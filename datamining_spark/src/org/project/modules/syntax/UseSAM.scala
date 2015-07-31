package org.project.modules.syntax

import javax.swing.JFrame
import javax.swing.JButton
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

object UseSAM {

  def main(args:Array[String]) {
    var data = 0
    val frame = new JFrame("SAM")
    val button = new JButton("Counter")
    
//    button.addActionListener(new ActionListener {
//      override def actionPerformed(event: ActionEvent) {
//    	  data += 1
//    	  println(data)
//      }
//    })
    
    implicit def convertActionEvent(actionEvent: ActionEvent => Unit) = new ActionListener {
      override def actionPerformed(event: ActionEvent) {
        actionEvent(event)
      }
    }
    
    button.addActionListener((event: ActionEvent) => {
      data += 1
      println(data)
    })
    
    frame.setContentPane(button)
    frame.pack()
    frame.setVisible(true)
    
  }
}