package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "cadastro", urlPatterns = {"/cadastro"})
public class cadastro extends HttpServlet {

    String url = "jdbc:derby://localhost:1527/trabalho";
    String usuarioBD = "eri";
    String senhaBD = "eri";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Cadastro</title></head><body>");
            out.println("<h2>Cadastro de Usuário</h2>");
            out.println("<form method='post' action='cadastro'>");
            out.println("Nome: <input type='text' name='nome'><br>");
            out.println("Email: <input type='email' name='email'><br>");
            out.println("Senha: <input type='password' name='senha'><br>");
            out.println("<input type='submit' value='Cadastrar'>");
            out.println("</form>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try (Connection con = DriverManager.getConnection(url, usuarioBD, senhaBD)) {
            // Inserir usuário
            String sqlUsuario = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, nome);
            pst.setString(2, email);
            pst.setString(3, senha);
            pst.executeUpdate();

            // Pegar ID gerado
            ResultSet rs = pst.getGeneratedKeys();
            int usuarioId = 0;
            if (rs.next()) {
                usuarioId = rs.getInt(1);
            }

            // Criar conta com número aleatório (exemplo simples)
            String numeroConta = "C" + (int)(Math.random() * 100000);
            String sqlConta = "INSERT INTO conta (numero, saldo, usuario_id) VALUES (?, 0, ?)";
            PreparedStatement pst2 = con.prepareStatement(sqlConta);
            pst2.setString(1, numeroConta);
            pst2.setInt(2, usuarioId);
            pst2.executeUpdate();

            // Redireciona para login
            response.sendRedirect("login");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Erro ao cadastrar: " + e.getMessage());
        }
    }
}
