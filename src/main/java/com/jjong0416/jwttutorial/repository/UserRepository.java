package com.jjong0416.jwttutorial.repository;


import com.jjong0416.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    // 유저 네임으로 User정보를 가져올 때 권한 정보도 같이 가져오게 한다는 뜻
    @EntityGraph(attributePaths = "authorities")// EntityGraph는 쿼리가 수행이 될 때, Lazy조회가 아니고 Eager 조회로 authorities 정보도 같이 가져오게 한다.
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
