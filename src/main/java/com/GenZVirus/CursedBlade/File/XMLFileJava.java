package com.GenZVirus.CursedBlade.File;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.GenZVirus.CursedBlade.CursedBlade;
import com.GenZVirus.CursedBlade.Common.Config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class XMLFileJava {

	private static final int default_kill_count = 0;
	private static final String default_player_uuid = "";

	private static final int default_life_steal = 0;
	private static final int default_poison = 0;
	private static final int default_wither = 0;
	private static final int default_hunger = 0;
	private static final int default_exhaust = 0;
	private static final String default_fire_aspect = "false";
	private static final String default_absorption_dest = "false";
	private static final String default_shield_dest = "false";

	public static String default_xmlFilePath = "CursedBlade/database.xml";

	public XMLFileJava() {

		try {

			File file = new File(default_xmlFilePath);
			File parent = file.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Couldn't create dir: " + parent);
			}

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			// Root
			Element root = document.createElement("Root");
			document.appendChild(root);

			// Enabled or Disabled
			Element starting_damage = document.createElement("KillCount");
			starting_damage.appendChild(document.createTextNode(Integer.toString(default_kill_count)));
			root.appendChild(starting_damage);

			// Enabled or Disabled
			Element life_steal = document.createElement("LifeSteal");
			life_steal.appendChild(document.createTextNode(Integer.toString(default_life_steal)));
			root.appendChild(life_steal);

			// Enabled or Disabled
			Element poison = document.createElement("Poison");
			poison.appendChild(document.createTextNode(Integer.toString(default_poison)));
			root.appendChild(poison);

			// Enabled or Disabled
			Element wither = document.createElement("Wither");
			wither.appendChild(document.createTextNode(Integer.toString(default_wither)));
			root.appendChild(wither);

			// Enabled or Disabled
			Element hunger = document.createElement("Hunger");
			hunger.appendChild(document.createTextNode(Integer.toString(default_hunger)));
			root.appendChild(hunger);

			// Enabled or Disabled
			Element exhaust = document.createElement("Exhaust");
			exhaust.appendChild(document.createTextNode(Integer.toString(default_exhaust)));
			root.appendChild(exhaust);

			// Enabled or Disabled
			Element fire_aspect = document.createElement("FireAspect");
			fire_aspect.appendChild(document.createTextNode(default_fire_aspect));
			root.appendChild(fire_aspect);

			// Enabled or Disabled
			Element absoption_dest = document.createElement("AbsorptionDestruction");
			absoption_dest.appendChild(document.createTextNode(default_absorption_dest));
			root.appendChild(absoption_dest);

			// Enabled or Disabled
			Element shield_dest = document.createElement("ShieldDestruction");
			shield_dest.appendChild(document.createTextNode(default_shield_dest));
			root.appendChild(shield_dest);

			// Enabled or Disabled
			Element player_uuid = document.createElement("PlayerUUID");
			player_uuid.appendChild(document.createTextNode(default_player_uuid));
			root.appendChild(player_uuid);

			// create the xml file
			// transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(default_xmlFilePath));

			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging

			transformer.transform(domSource, streamResult);

			System.out.println("Done creating XML File");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public static void editElement(String elementTag, String elementTextContent) {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(default_xmlFilePath);

			// Get root element

			checkFileElement(document, default_xmlFilePath, elementTag);
			Node element = document.getElementsByTagName(elementTag).item(0);
			element.setTextContent(elementTextContent);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(default_xmlFilePath));
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
	}

	public static String readElement(String elementTag) {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(default_xmlFilePath);
			checkFileElement(document, default_xmlFilePath, elementTag);
			Node element = document.getElementsByTagName(elementTag).item(0);
			return element.getTextContent();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return "0";
	}

	public static void checkFileElement(Document document, String xmlFilePath, String elementTag) {
		Node element = document.getElementsByTagName(elementTag).item(0);
		if (element == null) {
			try {
				element = document.createElement(elementTag);
				element.appendChild(document.createTextNode(Integer.toString(0)));
				Node root = document.getElementsByTagName("Root").item(0);
				root.appendChild(element);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(new File(xmlFilePath));
				transformer.transform(domSource, streamResult);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			resetElement(elementTag);
		}
	}

	public static void checkFileAndMake() {
		File file = new File(default_xmlFilePath);
		boolean found = file.exists();

		if (!found) {
			new XMLFileJava();
		}

	}

	public static void addOne(String elementTag) {
		XMLFileJava.editElement(elementTag, Integer.toString(Integer.parseInt(XMLFileJava.readElement(elementTag)) + 1));
	}

	public static void add(String elementTag, int value) {
		XMLFileJava.editElement(elementTag, Integer.toString(Integer.parseInt(XMLFileJava.readElement(elementTag)) + value));
	}

	public static void checkForUpgrades() {
		if (Integer.parseInt(XMLFileJava.readElement("KillCount")) % Config.COMMON.whenToUpgrade.get() == 0) {
			XMLFileJava.upgrade();
		}
	}

	public static void upgrade() {
		if (Boolean.parseBoolean(XMLFileJava.readElement("FireAspect")) && Boolean.parseBoolean(XMLFileJava.readElement("AbsorptionDestruction"))
				&& Boolean.parseBoolean(XMLFileJava.readElement("ShieldDestruction")) && Integer.parseInt(XMLFileJava.readElement("LifeSteal")) >= 100
				&& Integer.parseInt(XMLFileJava.readElement("Poison")) >= 255 && Integer.parseInt(XMLFileJava.readElement("Wither")) >= 255
				&& Integer.parseInt(XMLFileJava.readElement("Hunger")) >= 255 && Integer.parseInt(XMLFileJava.readElement("Exhaust")) >= 255)
			return;
		Random rand = new Random();
		int max = 8;
		int upgrade = rand.nextInt(max);
		switch (upgrade) {
		case 0: {
			if (Boolean.parseBoolean(XMLFileJava.readElement("FireAspect"))) {
				upgrade = rand.nextInt(max);
			} else {
				XMLFileJava.editElement("FireAspect", "true");
				break;
			}
		}
		case 1: {
			if (Boolean.parseBoolean(XMLFileJava.readElement("AbsorptionDestruction"))) {
				upgrade = rand.nextInt(max);
			} else {
				XMLFileJava.editElement("AbsorptionDestruction", "true");
				break;
			}
		}
		case 2: {
			if (Boolean.parseBoolean(XMLFileJava.readElement("ShieldDestruction"))) {
				upgrade = rand.nextInt(max);
			} else {
				XMLFileJava.editElement("ShieldDestruction", "true");
				break;
			}
		}
		case 3: {
			if (Integer.parseInt(XMLFileJava.readElement("LifeSteal")) < 100) {
				XMLFileJava.addOne("LifeSteal");
				break;
			} else {
				upgrade = rand.nextInt(max);
			}
		}
		case 4: {
			if (Integer.parseInt(XMLFileJava.readElement("Poison")) < 255) {
				XMLFileJava.addOne("Poison");
				break;
			} else {
				upgrade = rand.nextInt(max);
			}
		}
		case 5: {
			if (Integer.parseInt(XMLFileJava.readElement("Wither")) < 255) {
				XMLFileJava.addOne("Wither");
				break;
			} else {
				upgrade = rand.nextInt(max);
			}
		}
		case 6: {
			if (Integer.parseInt(XMLFileJava.readElement("Hunger")) < 255) {
				XMLFileJava.addOne("Hunger");
				break;
			} else {
				upgrade = rand.nextInt(max);
			}
		}
		case 7: {
			if (Integer.parseInt(XMLFileJava.readElement("Exhaust")) < 255) {
				XMLFileJava.addOne("Exhaust");
				break;
			} else {
				upgrade = rand.nextInt(max);
			}
		}
		default:
			break;
		}
	}

	public static void resetToDefault() {
		XMLFileJava.editElement("KillCount", Integer.toString(default_kill_count));
		XMLFileJava.editElement("LifeSteal", Integer.toString(default_life_steal));
		XMLFileJava.editElement("Poison", Integer.toString(default_poison));
		XMLFileJava.editElement("Wither", Integer.toString(default_wither));
		XMLFileJava.editElement("Hunger", Integer.toString(default_hunger));
		XMLFileJava.editElement("Exhaust", Integer.toString(default_exhaust));
		XMLFileJava.editElement("FireAspect", default_fire_aspect);
		XMLFileJava.editElement("AbsorptionDestruction", default_absorption_dest);
		XMLFileJava.editElement("ShieldDestruction", default_shield_dest);
	}

	private static void resetElement(String elementTag) {
		if (elementTag.equals("KillCount")) {
			XMLFileJava.editElement("KillCount", Integer.toString(default_kill_count));
		} else if (elementTag.equals("PlayerUUID")) {
			XMLFileJava.editElement("PlayerUUID", default_player_uuid);
		} else if (elementTag.equals("LifeSteal")) {
			XMLFileJava.editElement("LifeSteal", Integer.toString(default_life_steal));
		} else if (elementTag.equals("Poison")) {
			XMLFileJava.editElement("Poison", Integer.toString(default_poison));
		} else if (elementTag.equals("Wither")) {
			XMLFileJava.editElement("Wither", Integer.toString(default_wither));
		} else if (elementTag.equals("Hunger")) {
			XMLFileJava.editElement("Hunger", Integer.toString(default_hunger));
		} else if (elementTag.equals("Exhaust")) {
			XMLFileJava.editElement("Exhaust", Integer.toString(default_exhaust));
		} else if (elementTag.equals("AbsorptionDestruction")) {
			XMLFileJava.editElement("AbsorptionDestruction", default_absorption_dest);
		} else if (elementTag.equals("ShieldDestruction")) {
			XMLFileJava.editElement("ShieldDestruction", default_shield_dest);
		} else if (elementTag.equals("FireAspect")) {
			XMLFileJava.editElement("FireAspect", default_fire_aspect);
		}

	}

	public static void loadDamage() {
		CursedBlade.KILL_COUNTER = Integer.parseInt(XMLFileJava.readElement("KillCount"));
		CursedBlade.ATTACK_DAMAGE = (int) (Config.COMMON.starting_attack_damage.get() + CursedBlade.KILL_COUNTER * Config.COMMON.damage_ratio.get());
		CursedBlade.LIFE_STEAL = Integer.parseInt(XMLFileJava.readElement("LifeSteal"));
		CursedBlade.POISON = Integer.parseInt(XMLFileJava.readElement("Poison"));
		CursedBlade.WITHER = Integer.parseInt(XMLFileJava.readElement("Wither"));
		CursedBlade.HUNGER = Integer.parseInt(XMLFileJava.readElement("Hunger"));
		CursedBlade.EXHAUST = Integer.parseInt(XMLFileJava.readElement("Exhaust"));
		CursedBlade.FIRE_ASPECT = Boolean.parseBoolean(XMLFileJava.readElement("FireAspect"));
		CursedBlade.DESTROY_ABSORPTION = Boolean.parseBoolean(XMLFileJava.readElement("AbsorptionDestruction"));
		CursedBlade.DESTROY_SHIELDS = Boolean.parseBoolean(XMLFileJava.readElement("ShieldDestruction"));
		CursedBlade.STATUS = Config.COMMON.default_status.get();
	}

	public static void load(PlayerEntity player) {
		checkFileAndMake();
		CursedBlade.KILL_COUNTER = Integer.parseInt(XMLFileJava.readElement("KillCount"));
		CursedBlade.ATTACK_DAMAGE = (int) (Config.COMMON.starting_attack_damage.get() + CursedBlade.KILL_COUNTER * Config.COMMON.damage_ratio.get());
		CursedBlade.LIFE_STEAL = Integer.parseInt(XMLFileJava.readElement("LifeSteal"));
		CursedBlade.POISON = Integer.parseInt(XMLFileJava.readElement("Poison"));
		CursedBlade.WITHER = Integer.parseInt(XMLFileJava.readElement("Wither"));
		CursedBlade.HUNGER = Integer.parseInt(XMLFileJava.readElement("Hunger"));
		CursedBlade.EXHAUST = Integer.parseInt(XMLFileJava.readElement("Exhaust"));
		CursedBlade.FIRE_ASPECT = Boolean.parseBoolean(XMLFileJava.readElement("FireAspect"));
		CursedBlade.DESTROY_ABSORPTION = Boolean.parseBoolean(XMLFileJava.readElement("AbsorptionDestruction"));
		CursedBlade.DESTROY_SHIELDS = Boolean.parseBoolean(XMLFileJava.readElement("ShieldDestruction"));
		CursedBlade.STATUS = Config.COMMON.default_status.get();

		CompoundNBT nbt = player.serializeNBT().getCompound("ForgeCaps");
		if (nbt.getCompound("ageoftitans:stats").getInt("player_level") > 0) {
			CursedBlade.STATUS = "Awakened";
		}

	}

	public static void loadUUID() {
		if (XMLFileJava.readElement("PlayerUUID").equals(default_player_uuid))
			return;
		CursedBlade.PLAYER_UUID = UUID.fromString(XMLFileJava.readElement("PlayerUUID"));
	}

	public static void save() {
		checkFileAndMake();
		if (CursedBlade.PLAYER_UUID != null) {
			XMLFileJava.editElement("KillCount", Integer.toString(CursedBlade.KILL_COUNTER));
			XMLFileJava.editElement("LifeSteal", Integer.toString(CursedBlade.LIFE_STEAL));
			XMLFileJava.editElement("Poison", Integer.toString(CursedBlade.POISON));
			XMLFileJava.editElement("Wither", Integer.toString(CursedBlade.WITHER));
			XMLFileJava.editElement("Hunger", Integer.toString(CursedBlade.HUNGER));
			XMLFileJava.editElement("Exhaust", Integer.toString(CursedBlade.EXHAUST));
			XMLFileJava.editElement("FireAspect", Boolean.toString(CursedBlade.FIRE_ASPECT));
			XMLFileJava.editElement("AbsorptionDestruction", Boolean.toString(CursedBlade.DESTROY_ABSORPTION));
			XMLFileJava.editElement("ShieldDestruction", Boolean.toString(CursedBlade.DESTROY_SHIELDS));
			XMLFileJava.editElement("PlayerUUID", CursedBlade.PLAYER_UUID.toString());
		}
	}

}
