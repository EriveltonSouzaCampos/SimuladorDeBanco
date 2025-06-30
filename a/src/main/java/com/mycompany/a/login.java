package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "login", urlPatterns = {"/login"})
public class login extends HttpServlet {

    String url = "jdbc:derby://localhost:1527/trabalho";
    String usuarioBD = "eri";
    String senhaBD = "eri"; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Mensagem de erro vinda da sessão, se houver
        HttpSession sessao = request.getSession();
        String erro = (String) sessao.getAttribute("erroLogin");
        sessao.removeAttribute("erroLogin");

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Login</title></head><body>");
        out.println("<h2>Login</h2>");
        if (erro != null) {
            out.println("<p style='color:red'>" + erro + "</p>");
        }
        out.println("<form method='post' action='login'>");
        out.println("Email: <input type='text' name='email'><br>");
        out.println("Senha: <input type='password' name='senha'><br>");
        out.println("<input type='submit' value='Entrar'>");
        out.println("</form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try (Connection con = DriverManager.getConnection(url, usuarioBD, senhaBD)) {
            String sql = "SELECT id, nome FROM usuario WHERE email = ? AND senha = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, senha);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");

                // Salva na sessão
                HttpSession sessao = request.getSession();
                sessao.setAttribute("idUsuario", id);
                sessao.setAttribute("nomeUsuario", nome);

                // Redireciona para menu real
                response.sendRedirect("painel");

            } else {
                // Login inválido
                HttpSession sessao = request.getSession();
                sessao.setAttribute("erroLogin", "Email ou senha incorretos.");
                response.sendRedirect("login");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Erro no login: " + e.getMessage());
        }
    }
}
