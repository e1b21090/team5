package team5.game.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ZinrouAuthConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(AntPathRequestMatcher.antMatcher("/entry/**"))
            .authenticated() // /entry/以下は認証済みであること
            .requestMatchers(AntPathRequestMatcher.antMatcher("/**"))
            .permitAll()) // 上記以外は全員アクセス可能
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*"))) // h2-console用にCSRF対策を無効化
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                .sameOrigin()))
        .formLogin(login -> login
            .loginPage("/custom-login") // カスタムログインページを指定
            .loginProcessingUrl("/login") // ログイン処理を受け付けるURLを指定
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")); // ログアウト後に / にリダイレクト

    return http.build();
  }

  /**
   * 認証処理に関する設定（誰がどのようなロールでログインできるか）
   *
   * @return
   */
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    // ユーザ名，パスワード，ロールを指定してbuildする
    // このときパスワードはBCryptでハッシュ化されているため，{bcrypt}とつける
    // ハッシュ化せずに平文でパスワードを指定する場合は{noop}をつける
    // ハッシュ化されたパスワードを得るには，この授業のbashターミナルで下記のように末尾にユーザ名とパスワードを指定すると良い(要VPN)
    // $ sshrun htpasswd -nbBC 10 user1 p@ss

    UserDetails user1 = User.withUsername("user1")
        .password("{bcrypt}$2y$10$8PubbmvtsXaoe7uoiV7Ne.dUD5CmX6C2xZ2Jn8ohJ15crmRgSQ2XS").roles("USER").build();
    UserDetails user2 = User.withUsername("user2")
        .password("{bcrypt}$2y$10$8PubbmvtsXaoe7uoiV7Ne.dUD5CmX6C2xZ2Jn8ohJ15crmRgSQ2XS").roles("USER").build();
    UserDetails user3 = User.withUsername("user3")
        .password("{bcrypt}$2y$10$8PubbmvtsXaoe7uoiV7Ne.dUD5CmX6C2xZ2Jn8ohJ15crmRgSQ2XS").roles("USER").build();
    UserDetails user4 = User.withUsername("user4")
        .password("{bcrypt}$2y$10$8PubbmvtsXaoe7uoiV7Ne.dUD5CmX6C2xZ2Jn8ohJ15crmRgSQ2XS").roles("USER").build();

    // 生成したユーザをImMemoryUserDetailsManagerに渡す（いくつでも良い）
    return new InMemoryUserDetailsManager(user1, user2, user3, user4);
  }

}
