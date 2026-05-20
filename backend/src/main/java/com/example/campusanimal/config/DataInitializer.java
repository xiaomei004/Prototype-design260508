package com.example.campusanimal.config;

import com.example.campusanimal.entity.Animal;
import com.example.campusanimal.entity.Post;
import com.example.campusanimal.entity.User;
import com.example.campusanimal.repository.AnimalRepository;
import com.example.campusanimal.repository.PostRepository;
import com.example.campusanimal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDemoData(UserRepository userRepository,
                                   AnimalRepository animalRepository,
                                   PostRepository postRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            User admin = userRepository.save(User.builder()
                    .username("admin")
                    .password("123456")
                    .nickname("管理员")
                    .role("ADMIN")
                    .avatar(null)
                    .createTime(now.minusDays(7))
                    .build());

            User student01 = userRepository.save(User.builder()
                    .username("student01")
                    .password("123456")
                    .nickname("李同学")
                    .role("USER")
                    .avatar(null)
                    .createTime(now.minusDays(5))
                    .build());

            User student02 = userRepository.save(User.builder()
                    .username("student02")
                    .password("123456")
                    .nickname("张同学")
                    .role("USER")
                    .avatar(null)
                    .createTime(now.minusDays(4))
                    .build());

            Animal cat = animalRepository.save(Animal.builder()
                    .name("大橘")
                    .species("猫")
                    .gender("未知")
                    .color("橘色")
                    .location("北区食堂")
                    .status("已绝育")
                    .features("亲人,贪吃")
                    .imageUrl("/uploads/cat1.png")
                    .sterilized(1)
                    .vaccineStatus("未知")
                    .firstFoundTime(now.minusDays(20))
                    .auditStatus("APPROVED")
                    .createUserId(student01.getId())
                    .createTime(now.minusDays(20))
                    .build());

            Animal dogScholar = animalRepository.save(Animal.builder()
                    .name("学霸狗")
                    .species("狗")
                    .gender("未知")
                    .color("黄色")
                    .location("图书馆门口")
                    .status("健康")
                    .features("安静,怕生")
                    .imageUrl("/uploads/dog1.png")
                    .sterilized(0)
                    .vaccineStatus("未知")
                    .firstFoundTime(now.minusDays(18))
                    .auditStatus("APPROVED")
                    .createUserId(student01.getId())
                    .createTime(now.minusDays(18))
                    .build());

            Animal calico = animalRepository.save(Animal.builder()
                    .name("三花")
                    .species("猫")
                    .gender("未知")
                    .color("三花")
                    .location("南门草坪")
                    .status("未绝育")
                    .features("警惕,爱晒太阳")
                    .imageUrl("/uploads/cat1.png")
                    .sterilized(0)
                    .vaccineStatus("未知")
                    .firstFoundTime(now.minusDays(15))
                    .auditStatus("APPROVED")
                    .createUserId(student02.getId())
                    .createTime(now.minusDays(15))
                    .build());

            animalRepository.save(Animal.builder()
                    .name("小黄")
                    .species("狗")
                    .gender("未知")
                    .color("黄色")
                    .location("体育馆侧门")
                    .status("待观察")
                    .features("活泼,亲人")
                    .imageUrl("/uploads/dog2.png")
                    .sterilized(0)
                    .vaccineStatus("未知")
                    .firstFoundTime(now.minusDays(10))
                    .auditStatus("PENDING")
                    .createUserId(student02.getId())
                    .createTime(now.minusDays(10))
                    .build());

            postRepository.save(Post.builder()
                    .userId(student01.getId())
                    .animalId(cat.getId())
                    .type("feed")
                    .content("在北区食堂发布了“大橘”相关动态")
                    .imageUrl("/uploads/cat1.png")
                    .location("北区食堂")
                    .createTime(now.minusMinutes(11))
                    .build());

            postRepository.save(Post.builder()
                    .userId(student02.getId())
                    .animalId(calico.getId())
                    .type("checkin")
                    .content("南门草坪已补充猫粮和清水。")
                    .imageUrl(null)
                    .location("南门草坪")
                    .createTime(now.minusMinutes(35))
                    .build());

            postRepository.save(Post.builder()
                    .userId(student01.getId())
                    .animalId(null)
                    .type("rescue")
                    .content("教学楼B区发现疑似受伤幼猫，正在联系救助。")
                    .imageUrl(null)
                    .location("教学楼B区")
                    .createTime(now.minusMinutes(51))
                    .build());
        };
    }
}
