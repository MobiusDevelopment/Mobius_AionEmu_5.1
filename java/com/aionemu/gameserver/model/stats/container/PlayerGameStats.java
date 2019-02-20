/*
 * This file is part of the Aion-Emu project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.ride.RideInfo;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	private int cachedSpeed;
	private int cachedAttackSpeed;
	
	/**
	 * @param owner
	 */
	public PlayerGameStats(Player owner)
	{
		super(owner);
	}
	
	@Override
	protected void onStatsChange()
	{
		super.onStatsChange();
		updateStatsAndSpeedVisually();
	}
	
	public void updateStatsAndSpeedVisually()
	{
		updateStatsVisually();
		checkSpeedStats();
	}
	
	public void updateStatsVisually()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}
	
	private void checkSpeedStats()
	{
		final int current = getMovementSpeed().getCurrent();
		final int currentAttackSpeed = getAttackSpeed().getCurrent();
		if ((current != cachedSpeed) || (currentAttackSpeed != cachedAttackSpeed))
		{
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
		cachedAttackSpeed = currentAttackSpeed;
	}
	
	@Override
	public Stat2 getMaxHp()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXHP, pst.getMaxHp());
	}
	
	@Override
	public Stat2 getMaxMp()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXMP, pst.getMaxMp());
	}
	
	@Override
	public Stat2 getStrikeResist()
	{
		return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, 0);
	}
	
	@Override
	public Stat2 getStrikeFort()
	{
		return getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0);
	}
	
	@Override
	public Stat2 getSpellResist()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0);
	}
	
	@Override
	public Stat2 getSpellFort()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0);
	}
	
	public Stat2 getMaxDp()
	{
		return getStat(StatEnum.MAXDP, 4000);
	}
	
	public Stat2 getFlyTime()
	{
		return getStat(StatEnum.FLY_TIME, CustomConfig.BASE_FLYTIME);
	}
	
	@Override
	public Stat2 getAllSpeed()
	{
		return getStat(StatEnum.ALLSPEED, 7500);
	}
	
	@Override
	public Stat2 getAttackSpeed()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = (int) pst.getAttackSpeed() * 1500;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
			final Item offWeapon = owner.getEquipment().getOffHandWeapon();
			if (offWeapon != null)
			{
				base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
			}
		}
		final Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
		return aSpeed;
	}
	
	@Override
	public Stat2 getBCastingTime()
	{
		int base = 0;
		final int casterClass = owner.getPlayerClass().getClassId();
		if ((casterClass == 7) || // Sorcerer.
			(casterClass == 8) || // Spirit-Master.
			(casterClass == 16)) // Songweaver.
		{
			base = 800;
		}
		return getStat(StatEnum.BOOST_CASTING_TIME, base);
	}
	
	@Override
	public Stat2 getConcentration()
	{
		int base = 0;
		final int sorcerer1 = owner.getPlayerClass().getClassId();
		final int spiritMaster1 = owner.getPlayerClass().getClassId();
		if (sorcerer1 == 7)
		{
			base = 25;
		}
		else if ((spiritMaster1 == 8) && (owner.getLevel() >= 56))
		{
			base = 100;
		}
		return getStat(StatEnum.CONCENTRATION, base);
	}
	
	@Override
	public Stat2 getRootResistance()
	{
		int base = 0;
		final int aethertech2 = owner.getPlayerClass().getClassId();
		if (aethertech2 == 13)
		{
			base = 200;
		}
		return getStat(StatEnum.ROOT_RESISTANCE, base);
	}
	
	@Override
	public Stat2 getSnareResistance()
	{
		int base = 0;
		final int aethertech3 = owner.getPlayerClass().getClassId();
		if (aethertech3 == 13)
		{
			base = 200;
		}
		return getStat(StatEnum.SNARE_RESISTANCE, base);
	}
	
	@Override
	public Stat2 getBindResistance()
	{
		int base = 0;
		final int aethertech4 = owner.getPlayerClass().getClassId();
		if (aethertech4 == 13)
		{
			base = 200;
		}
		return getStat(StatEnum.BIND_RESISTANCE, base);
	}
	
	@Override
	public Stat2 getFearResistance()
	{
		int base = 0;
		final int aethertech5 = owner.getPlayerClass().getClassId();
		if (aethertech5 == 13)
		{
			base = -200;
		}
		return getStat(StatEnum.FEAR_RESISTANCE, base);
	}
	
	@Override
	public Stat2 getSleepResistance()
	{
		int base = 0;
		final int aethertech6 = owner.getPlayerClass().getClassId();
		if (aethertech6 == 13)
		{
			base = -200;
		}
		return getStat(StatEnum.SLEEP_RESISTANCE, base);
	}
	
	@Override
	public Stat2 getPDef()
	{
		int base = 0;
		final int gunslinger = owner.getPlayerClass().getClassId();
		final int aethertech = owner.getPlayerClass().getClassId();
		if (gunslinger == 14)
		{
			base = 100;
		}
		else if (aethertech == 13)
		{
			base = 350;
		}
		return getStat(StatEnum.PHYSICAL_DEFENSE, base);
	}
	
	@Override
	public Stat2 getMResist()
	{
		int base = 0;
		final int assassin = owner.getPlayerClass().getClassId();
		if ((assassin == 4) && (owner.getLevel() >= 37))
		{
			base = 30;
		}
		return getStat(StatEnum.MAGICAL_RESIST, base);
	}
	
	@Override
	public Stat2 getMBResist()
	{
		int base = 0;
		final int cleric = owner.getPlayerClass().getClassId();
		final int sorcerer2 = owner.getPlayerClass().getClassId();
		final int spiritMaster2 = owner.getPlayerClass().getClassId();
		if ((cleric == 10) && (owner.getLevel() >= 60))
		{
			base = 140;
		}
		if ((sorcerer2 == 7) && (owner.getLevel() >= 60))
		{
			base = 180;
		}
		if ((spiritMaster2 == 8) && (owner.getLevel() >= 60))
		{
			base = 180;
		}
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}
	
	@Override
	public Stat2 getMovementSpeed()
	{
		Stat2 movementSpeed;
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		if (owner.isInPlayerMode(PlayerMode.RIDE))
		{
			final RideInfo ride = owner.ride;
			final int runSpeed = (int) pst.getRunSpeed() * 1000;
			if (owner.isInState(CreatureState.FLYING))
			{
				movementSpeed = new AdditionStat(StatEnum.FLY_SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int) (ride.getFlySpeed() * 1000) - runSpeed);
			}
			else
			{
				final float speed = owner.isInSprintMode() ? ride.getSprintSpeed() : ride.getMoveSpeed();
				movementSpeed = new AdditionStat(StatEnum.SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int) (speed * 1000) - runSpeed);
			}
		}
		else if (owner.isInFlyingState())
		{
			movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000));
		}
		else if (owner.isInState(CreatureState.FLIGHT_TELEPORT) && !owner.isInState(CreatureState.RESTING))
		{
			movementSpeed = getStat(StatEnum.SPEED, 12000);
		}
		else if (owner.isInState(CreatureState.WALKING))
		{
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000));
		}
		else if (getAllSpeed().getBonus() != 0)
		{
			movementSpeed = getStat(StatEnum.SPEED, getAllSpeed().getCurrent());
		}
		else
		{
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000));
		}
		return movementSpeed;
	}
	
	@Override
	public Stat2 getAttackRange()
	{
		int base = 1500;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if (mainHandWeapon != null)
		{
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
			if (!mainHandWeapon.getItemTemplate().isTwoHandWeapon() && (mainHandWeapon != null) && (offHandWeapon != null) && (offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD))
			{
				if (mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange() != offHandWeapon.getItemTemplate().getWeaponStats().getAttackRange())
				{
					if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H))
					{
						base = 1500;
					}
					else
					{
						if ((mainHandWeapon != null) && (offHandWeapon != null) && (offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD))
						{
							base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
							log.info("[Error] PlayerGameStats] mainHandWeapon [" + mainHandWeapon.getItemTemplate().getItemType() + "] offHandWeapon [" + offHandWeapon.getItemTemplate().getItemType() + "]");
						}
					}
				}
			}
		}
		return getStat(StatEnum.ATTACK_RANGE, base);
	}
	
	@Override
	public Stat2 getMDef()
	{
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}
	
	@Override
	public Stat2 getPower()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.POWER, pst.getPower());
	}
	
	@Override
	public Stat2 getHealth()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.HEALTH, pst.getHealth());
	}
	
	@Override
	public Stat2 getAccuracy()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.ACCURACY, pst.getAccuracy());
	}
	
	@Override
	public Stat2 getAgility()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.AGILITY, pst.getAgility());
	}
	
	@Override
	public Stat2 getKnowledge()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
	}
	
	@Override
	public Stat2 getWill()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.WILL, pst.getWill());
	}
	
	@Override
	public Stat2 getEvasion()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.EVASION, pst.getEvasion());
	}
	
	@Override
	public Stat2 getParry()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getParry();
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
		}
		return getStat(StatEnum.PARRY, base);
	}
	
	@Override
	public Stat2 getBlock()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.BLOCK, pst.getBlock());
	}
	
	@Override
	public Stat2 getMainHandPAttack()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAttack();
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (mainHandWeapon.getItemTemplate().getAttackType().isMagical())
			{
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, owner);
			}
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		final Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_POWER, stat);
	}
	
	public Stat2 getOffHandPAttack()
	{
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.98;
			final Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, owner);
	}
	
	@Override
	public Stat2 getMainHandPCritical()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), (owner.getLevel()));
		int base = pst.getMainHandCritRate();
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (mainHandWeapon.hasFusionedItem())
			{
				base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical() + mainHandWeapon.getFusionedItemTemplate().getWeaponStats().getPhysicalCritical();
			}
			else
			{
				base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
			}
		}
		return getStat(StatEnum.PHYSICAL_CRITICAL, base);
	}
	
	public Stat2 getOffHandPCritical()
	{
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			final int base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
			return getStat(StatEnum.PHYSICAL_CRITICAL, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, owner);
	}
	
	@Override
	public Stat2 getMainHandPAccuracy()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAccuracy();
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
		}
		return getStat(StatEnum.PHYSICAL_ACCURACY, base);
	}
	
	public Stat2 getOffHandPAccuracy()
	{
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
			int base = pst.getMainHandAccuracy();
			base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
			return getStat(StatEnum.PHYSICAL_ACCURACY, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, owner);
	}
	
	@Override
	public Stat2 getMAttack()
	{
		int base;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical())
			{
				return new AdditionStat(StatEnum.MAGICAL_ATTACK, 0, owner);
			}
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		else
		{
			base = Rnd.get(16, 20);
		}
		return getStat(StatEnum.MAGICAL_ATTACK, base);
	}
	
	@Override
	public Stat2 getMainHandMAttack()
	{
		int base = 0;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical())
			{
				return new AdditionStat(StatEnum.MAIN_HAND_MAGICAL_POWER, 0, owner);
			}
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		final Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_MAGICAL_POWER, stat);
	}
	
	@Override
	public Stat2 getOffHandMAttack()
	{
		int base = 0;
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.82;
			final Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_MAGICAL_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_POWER, 0, owner);
	}
	
	@Override
	public Stat2 getMBoost()
	{
		int base = 0;
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
		}
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
	}
	
	@Override
	public Stat2 getMAccuracy()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMagicAccuracy();
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
		}
		return getStat(StatEnum.MAGICAL_ACCURACY, base);
	}
	
	@Override
	public Stat2 getMCritical()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		final int base = pst.getMCritical();
		return getStat(StatEnum.MAGICAL_CRITICAL, base);
	}
	
	@Override
	public Stat2 getHpRegenRate()
	{
		int base = owner.getLevel() + 3;
		if (owner.isInState(CreatureState.RESTING))
		{
			base *= 8;
		}
		base *= getHealth().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}
	
	@Override
	public Stat2 getMpRegenRate()
	{
		int base = owner.getLevel() + 8;
		if (owner.isInState(CreatureState.RESTING))
		{
			base *= 8;
		}
		base *= getWill().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_MP, base);
	}
	
	@Override
	public void updateStatInfo()
	{
		PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
	}
	
	@Override
	public void updateSpeedInfo()
	{
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
	}
}