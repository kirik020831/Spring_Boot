package springboot.SpringBoot.service;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import springboot.SpringBoot.dao.RoleRepository;
import springboot.SpringBoot.model.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> listRole() {
        return roleRepository.findAll();
    }

    @Override
    public void add(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void update(Role role) {
        roleRepository.save(role);
    }

    @Override
    public Role getById(int id) {
        Role role = null;
        Optional<Role> opt = roleRepository.findById(id);
        if (opt.isPresent()) {
            role = opt.get();
        }
        return role;
    }

    @Override
    public Role getByName(String roleName) throws NotFoundException {
        Role role = roleRepository.findByRole(roleName);
        if (role == null) {
            throw new NotFoundException(roleName);
        }
        return role;
    }

    @Override
    public Set<Role> getRoleSet(String[] role) throws NotFoundException {
        Set<Role> roleSet = new HashSet<>();
        for (String roles : role) {
            roleSet.add(getByName(roles));
        }
        return roleSet;
    }
}
