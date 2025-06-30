package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "saldo", urlPatterns = {"/saldo"})
public class Saldo extends HttpServlet {

    String url = "jdbc:derby://localhost:1527/trabalho";
    String usuario = "eri";
    String senhaBD = "eri";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            response.sendRedirect("login");
            return;
        }

        int idUsuario = (int) sessao.getAttribute("idUsuario");
        double saldo = 0;
        String numeroConta = "";

        try (Connection con = DriverManager.getConnection(url, usuario, senhaBD)) {
            String sql = "SELECT numero, saldo FROM conta WHERE usuario_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idUsuario);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                numeroConta = rs.getString("numero");
                saldo = rs.getDouble("saldo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Saldo</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; background: #f4f4f4; text-align: center; padding-top: 50px; }");
            out.println(".card { background: white; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.1); display: inline-block; }");
            out.println("h2 { color: #333; }");
            out.println("</style>");
            out.println("</head><body>");
            out.println("<div class='card'>");
            out.println("<h2>Conta: " + numeroConta + "</h2>");
            out.println("<h3>Saldo disponível:</h3>");
            out.println("<p style='font-size: 24px; font-weight: bold; color: green;'>R$ " + String.format("%.2f", saldo) + "</p>");
            out.println("<br><a href='painel'>Voltar ao Painel</a>");
            out.println("</div>");
            out.println("</body></html>");
        }
    }
}
