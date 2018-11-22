package be.belga.reporter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.belga.reporter.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findOneByUsername(String username);

	public User findByUsernameAndPassword(String username, String password);

	public User findByEmail(String email);

}
