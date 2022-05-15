package hongik.ce.jolup.service;

import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.repository.BelongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class BelongService {

    private final BelongRepository belongRepository;

    public Long saveBelong(BelongDto belongDto) {
        return belongRepository.save(belongDto.toEntity()).getId();
    }

    public void deleteBelong(Long belongId) {
        belongRepository.deleteById(belongId);
    }
}
