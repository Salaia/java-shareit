package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
                user.setEmail(userDto.getEmail());
            }

            user = userRepository.save(user);
            return UserMapper.toUserDto(user);
        } else {
            throw new EntityNotFoundException("Not found: user by id " + userDto.getId());
        }
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.userDtoList(users);
    }

    @Override
    public UserDto findUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return UserMapper.toUserDto(userOptional.get());
        } else {
            throw new EntityNotFoundException("Not found: user by id " + id);
        }
    }

    @Override
    public void removeUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            throw new EntityNotFoundException("Not found: user by id " + id);
        }
    }
}
