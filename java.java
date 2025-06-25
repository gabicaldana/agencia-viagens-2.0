// Validação documentos
                  String tipo = (String) cbTipo.getSelectedItem();

               // Validação NACIONAL (11 dígitos)
               if ("NACIONAL".equals(tipo)) {
                   if (!documento.matches("\\d{11}")) {
                       JOptionPane.showMessageDialog(dialog, "CPF deve conter 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
               } 
               // Validação ESTRANGEIRO (2 letras + 6 números)
               else if ("ESTRANGEIRO".equals(tipo)) {
                   if (!documento.matches("[A-Za-z]{2}\\d{6}")) {
                       JOptionPane.showMessageDialog(dialog, "Passaporte deve conter 2 letras seguidas de 6 números (ex: AB123456).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                       return;
                   }
               }
