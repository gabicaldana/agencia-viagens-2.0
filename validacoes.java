 //Validações
        btnSalvarDialogo.addActionListener(e -> {
        	  String nome = txtNome.getText().trim();
              String telefone = txtTelefone.getText().trim();
              String email = txtEmail.getText().trim();
              String documento = txtDocumento.getText().trim();
              StringBuilder erros = new StringBuilder();
              
              if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                  JOptionPane.showMessageDialog(dialog, "Todos os campos são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                  return;
              }
              
              //validação nome
              if (!nome.matches("[a-zA-Z ]+")) {
                  erros.append("- Use apenas letras no nome\n");
              }
              
              //validação telefone
              if (!telefone.matches("\\d+")) {
                  JOptionPane.showMessageDialog(dialog, "Telefone deve conter apenas números.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                  return;
              }
              
              //validação email
              if (!email.isEmpty()) {
                  String[] partes = email.split("@");
                  if (partes.length != 2 || 
                      partes[0].isEmpty() || 
                      !partes[1].contains(".") ||
                      partes[1].indexOf(".") == 0 ||
                      partes[1].endsWith(".")) {
                      erros.append("- Email inválido. Use o formato: nome@dominio.com\n");
                  }
              }
              
              //validação documentos
              String tipo = (String) cbTipo.getSelectedItem();
              if ("NACIONAL".equals(tipo) && !documento.matches("\\d{11}")) {
                  JOptionPane.showMessageDialog(dialog, "CPF deve conter 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                  return;
              }
        });
