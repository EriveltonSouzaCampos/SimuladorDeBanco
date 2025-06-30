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

        HttpSession sessao = request.getSession();
        String erro = (String) sessao.getAttribute("erroLogin");
        sessao.removeAttribute("erroLogin");

        out.println("<!DOCTYPE html><html lang='pt-br'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Login – Banco Campos</title>");
        out.println("<style>");
        out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--text:#fff;--muted:#cfd6e1}");
        out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif}");
        out.println("body{background:var(--bg);color:var(--text);min-height:100vh;display:flex;flex-direction:column}");
        out.println("header{display:flex;justify-content:space-between;align-items:center;padding:20px 8%;background:rgba(0,0,0,.3)}");
        out.println(".logo{font-size:1.8rem;font-weight:700;color:var(--accent)}");
        out.println(".btn{background:var(--accent);color:#000;padding:10px 20px;border:none;border-radius:6px;cursor:pointer;font-weight:600;transition:.3s}");
        out.println(".btn:hover{background:#b48f2e;transform:translateY(-2px)}");
        out.println(".card{background:#13273a;padding:40px 32px;border-radius:10px;box-shadow:0 10px 18px rgba(0,0,0,.4);max-width:380px;margin:100px auto;width:90%}");
        out.println("h2{text-align:center;margin-bottom:24px;color:var(--accent)}");
        out.println("input{width:100%;padding:12px 14px;margin:10px 0;background:#0f2236;border:1px solid #334861;border-radius:6px;color:var(--text)}");
        out.println("input::placeholder{color:var(--muted)}");
        out.println("form button{width:100%;margin-top:14px}");
        out.println(".erro{color:#e74c3c;text-align:center;margin-bottom:12px}");
        out.println("a.link{display:block;text-align:center;color:var(--muted);margin-top:14px;text-decoration:none}");
        out.println("a.link:hover{color:var(--accent)}");
        out.println("</style></head><body>");

        // HEADER
        out.println("<header>");
        out.println("<div class='logo'>Banco Campos</div>");
        out.println("<button class='btn' onclick=\"location.href='cadastro'\">Sign‑up</button>");
        out.println("</header>");

        // CARD LOGIN
        out.println("<div class='card'>");
        out.println("<h2>Login</h2>");
        if (erro != null) out.println("<div class='erro'>"+erro+"</div>");
        out.println("<form method='post' action='login'>");
        out.println("<input type='text' name='email' placeholder='Email' required>");
        out.println("<input type='password' name='senha' placeholder='Senha' required>");
        out.println("<button class='btn' type='submit'>Entrar</button>");
        out.println("</form>");
        out.println("<a class='link' href='menu'>&larr; Voltar ao início</a>");
        out.println("</div>");

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
