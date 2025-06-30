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
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='pt-br'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Cadastro – Banco Campos</title>");
        out.println("<style>");
        out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--text:#fff;--muted:#cfd6e1}");
        out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif}");
        out.println("body{background:var(--bg);color:var(--text);min-height:100vh;display:flex;flex-direction:column}");
        out.println("header{display:flex;justify-content:space-between;align-items:center;padding:20px 8%;background:rgba(0,0,0,.3)}");
        out.println(".logo{font-size:1.8rem;font-weight:700;color:var(--accent)}");
        out.println(".btn{background:var(--accent);color:#000;padding:10px 20px;border:none;border-radius:6px;cursor:pointer;font-weight:600;transition:.3s}");
        out.println(".btn:hover{background:#b48f2e;transform:translateY(-2px)}");
        out.println(".card{background:#13273a;padding:40px 32px;border-radius:10px;box-shadow:0 10px 18px rgba(0,0,0,.4);max-width:400px;margin:100px auto;width:90%}");
        out.println("h2{text-align:center;margin-bottom:24px;color:var(--accent)}");
        out.println("input{width:100%;padding:12px 14px;margin:10px 0;background:#0f2236;border:1px solid #334861;border-radius:6px;color:var(--text)}");
        out.println("input::placeholder{color:var(--muted)}");
        out.println("form button{width:100%;margin-top:14px}");
        out.println("a.link{display:block;text-align:center;color:var(--muted);margin-top:14px;text-decoration:none}");
        out.println("a.link:hover{color:var(--accent)}");
        out.println("</style></head><body>");

        // HEADER
        out.println("<header>");
        out.println("<div class='logo'>Banco Campos</div>");
        out.println("<button class='btn' onclick=\"location.href='login'\">Login</button>");
        out.println("</header>");

        // FORM
        out.println("<div class='card'>");
        out.println("<h2>Cadastro</h2>");
        out.println("<form method='post' action='cadastro'>");
        out.println("<input type='text' name='nome' placeholder='Nome completo' required>");
        out.println("<input type='email' name='email' placeholder='Email' required>");
        out.println("<input type='password' name='senha' placeholder='Senha' required>");
        out.println("<button class='btn' type='submit'>Cadastrar</button>");
        out.println("</form>");
        out.println("<a class='link' href='menu'>&larr; Voltar ao início</a>");
        out.println("</div>");

        out.println("</body></html>");
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
