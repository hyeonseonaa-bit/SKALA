package com.sk.skala.stockapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Player;

// JpaRepository<Player, String>를 상속
// Player 엔터티를 관리하며, PK 타입은 String (playerId)
public interface PlayerRepository extends JpaRepository<Player, String> {
}

// 기본 메서드 참고
// - findById(playerId): 플레이어 ID로 조회
// - save(player): 플레이어 저장 및 수정
// - delete(player): 플레이어 삭제