package org.project.modules.syntax

import scala.swing.SimpleSwingApplication
import scala.swing.Button
import scala.swing.Label
import scala.swing.FlowPanel
import scala.swing.MainFrame
import scala.swing.event.ButtonClicked
import scala.swing.FileChooser
import java.io.File
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.Swing

object UseGUI extends SimpleSwingApplication {

  val fileChooser = new FileChooser(new File("."))
  fileChooser.title = "file chooser"
  
  val button = new Button {
    text = "click"
  }
  
  val label = new Label {
    text = "show content"
  }
  
  val boxPanel = new BoxPanel(Orientation.Vertical) {
    contents += button
    contents += label
    border = Swing.EmptyBorder(50, 50, 50, 50)
  }
  
  val mainPanel = new FlowPanel {
    contents += button
    contents += label
  }
  
  def top = new MainFrame {
    title = "Main Frame"
    contents = mainPanel
    
    listenTo(button)
    
    reactions += {
      case ButtonClicked(buttone) => {
        val result = fileChooser.showOpenDialog(mainPanel)
        if (result == FileChooser.Result.Approve) {
          label.text = fileChooser.selectedFile.getPath()
        }
      }
    }
    
  }
  
  
}